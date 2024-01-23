package com.example.productkanbanapi.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.productkanbanapi.entity.FgChecklist;
import com.example.productkanbanapi.entity.NotInStorage;
import com.example.productkanbanapi.entity.XtendMaterialtransactions;
import com.example.productkanbanapi.mapper.Fg313notrecMapper;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.utils.SapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
            for (NotInStorage check : checkList) {
                SapUtils sapUtils = new SapUtils();
                List<String[]> postInfoList = sapUtils.getPostInfo(check.getPlant(), check.getPartNumber(), check.getBatch(), "313");
                if (!CollectionUtils.isEmpty(postInfoList)) {
                    QueryWrapper<XtendMaterialtransactions> transQueryWrapper = new QueryWrapper<>();
                    transQueryWrapper.eq("UID", check.getUid());
                    String isUidPost = kanbanMapper.findTransByUid(transQueryWrapper);
                    if (!"1".equals(isUidPost)) {
                        notRec313List.add(check);
                    }
                }
            }
        }
        int is313NotRecDeleted = kanbanMapper.delAll313NotRec();
        log.info(is313NotRecDeleted != 0 ? "删除转运未收货数据" + is313NotRecDeleted + "条" : "删除失败或表无数据（转运未收货数据）");
        if (!CollectionUtils.isEmpty(notRec313List)) {
            fg313notrecMapper.insert(notRec313List);
            long end = System.currentTimeMillis();
            log.info("耗时" + (end - start) + "ms");
        }
    }

}
