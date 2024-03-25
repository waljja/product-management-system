package com.example.productkanbanapi.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.productkanbanapi.entity.FgChecklist;
import com.example.productkanbanapi.entity.NotInStorage;
import com.example.productkanbanapi.entity.XtendMaterialtransactions;
import com.example.productkanbanapi.mapper.Fg313notrecMapper;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.utils.SapUtils;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 保存313未收货数据
 *
 * @author 丁国钊
 * @date 2024-1-19 10:34
 */
@Slf4j
@Component
public class save313NotRecInfoJob extends QuartzJobBean {

    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Autowired
    KanbanMapper kanbanMapper;
    @Autowired
    Fg313notrecMapper fg313notrecMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        long start = System.currentTimeMillis();
        List<NotInStorage> notRec313List = new ArrayList<>();
        // 获取3天前送检单数据
        QueryWrapper<FgChecklist> checklistQueryWrapper = new QueryWrapper<>();
        checklistQueryWrapper.eq("status", 01)
                .apply("date_sub(curdate(), interval 3 day) <= date(update_date)");
        List<NotInStorage> checkList = kanbanMapper.findPdsInfoFrom3DaysAgo(checklistQueryWrapper);
        if (!CollectionUtils.isEmpty(checkList)) {
            // 物料号+批次+SAP厂区 去重
            List<NotInStorage> distinctCheckList = checkList.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                                    new TreeSet<>(Comparator.comparing(o ->
                                            o.getPartNumber() + "," + o.getPlant() + "," + o.getBatch()))),
                            ArrayList::new));
            // 过滤出不同工厂数据
            List<NotInStorage> checkList1100 = distinctCheckList.stream()
                    .filter(check -> "1100".equals(check.getPlant()))
                    .collect(Collectors.toList());
            // 物料、批次去重后传入SAP
            String[] partNumberArr = checkList1100.stream().map(NotInStorage::getPartNumber).toArray(String[]::new);
            String[] batchArr = checkList1100.stream().map(NotInStorage::getBatch).toArray(String[]::new);
            SapUtils sapUtils = new SapUtils();
            String transactionType = "313";
            List<String[]> postInfoArrList = sapUtils.getPostInfoBatch("1100", partNumberArr, batchArr, transactionType);
            List<NotInStorage> postInfoList = new ArrayList<>();
            postInfoArrList.forEach(arr -> {
                NotInStorage postInfo = new NotInStorage();
                postInfo.setPartNumber(arr[1]);
                postInfo.setBatch(arr[9]);
                postInfoList.add(postInfo);
            });
            // 过滤出成品送检表中在转运中的数据
            List<NotInStorage> transferList = checkList1100.stream()
                    .filter(listA ->
                            postInfoList.stream().anyMatch(listB ->
                                    Objects.equals(listA.getPartNumber(), listB.getPartNumber()) &&
                                            Objects.equals(listA.getBatch(), listB.getBatch())))
                    .collect(Collectors.toList());
            List<NotInStorage> uidList = new ArrayList<>();
            QueryWrapper<XtendMaterialtransactions> transQueryWrapper = new QueryWrapper<>();
            // 获取转运已收货UID
            if (transferList.size() > 1000) {
                // 1000条一组in查询
                List<List<NotInStorage>> splitParams = ListUtils.partition(transferList, 999);
                for (List<NotInStorage> splitParam : splitParams) {
                    transQueryWrapper = new QueryWrapper<>();
                    transQueryWrapper.in("UID", splitParam.stream().map(NotInStorage::getUid).toArray());
                    List<String> uids = kanbanMapper.findTransByUid(transQueryWrapper);
                    if (!CollectionUtils.isEmpty(uids)) {
                        uids.forEach(uid -> {
                            NotInStorage notInStorage = new NotInStorage();
                            notInStorage.setUid(uid);
                            uidList.add(notInStorage);
                        });
                    }
                }
            } else {
                transQueryWrapper.in("UID", transferList.stream().map(NotInStorage::getUid).toArray());
                List<String> uids = kanbanMapper.findTransByUid(transQueryWrapper);
                if (!CollectionUtils.isEmpty(uids)) {
                    uids.forEach(uid -> {
                        NotInStorage notInStorage = new NotInStorage();
                        notInStorage.setUid(uid);
                        uidList.add(notInStorage);
                    });
                }
            }
            // 转运未收货数据（未做315）
            // guava计算差集，获取转运未收货数据
            Set<NotInStorage> set1 = Sets.newHashSet(transferList);
            Set<NotInStorage> set2 = Sets.newHashSet(uidList);
            Set<NotInStorage> difference = Sets.difference(set1, set2);
            notRec313List = new ArrayList<>(difference);
        }
        int is313NotRecDeleted = kanbanMapper.delAll313NotRec();
        log.info(is313NotRecDeleted != 0 ? "删除转运未收货数据" + is313NotRecDeleted + "条" : "删除失败或表无数据（转运未收货数据）");
        if (!CollectionUtils.isEmpty(notRec313List)) {
            // 1000条一组批量插入
            List<List<NotInStorage>> splitList = ListUtils.partition(notRec313List, 100);
            splitList.forEach(list -> fg313notrecMapper.insert(list));
            long end = System.currentTimeMillis();
            log.info("耗时" + (end - start) + "ms");
        }
    }

}
