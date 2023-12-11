package com.ktg.mes.fg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ktg.common.utils.DateUtils;
import com.ktg.mes.fg.domain.FgInventory;
import com.ktg.mes.fg.mapper.FgInventoryMapper;
import com.ktg.mes.fg.service.FgInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 成品上架清单Service业务层处理
 *
 * @author JiangTingming
 * @date 2023-05-05
 */
@Service
public class FgInventoryServiceImpl implements FgInventoryService {
    @Autowired
    private FgInventoryMapper fgInventoryMapper;

    /**
     * 查询成品库存
     *
     * @param id 成品库存主键
     * @return 成品库存
     */
    @Override
    public FgInventory selectFgInventoryById(Long id)
    {
        return fgInventoryMapper.selectFgInventoryById(id);
    }

    /**
     * 查询成品库存列表
     *
     * @param fgInventory 成品库存
     * @return 成品库存
     */
    @Override
    public List<FgInventory> selectFgInventoryList(FgInventory fgInventory)
    {
        return fgInventoryMapper.selectFgInventoryList(fgInventory);
    }

    @Override
    public List<FgInventory> selectFgInventoryList2(FgInventory fgInventory)
    {
        return fgInventoryMapper.selectFgInventoryList2(fgInventory);
    }


    /**
     * 新增成品库存
     *
     * @param fgInventory 成品库存
     * @return 结果
     */
    @Override
    public int insertFgInventory(FgInventory fgInventory)
    {
        fgInventory.setCreateTime(DateUtils.getNowDate());
        return fgInventoryMapper.insertFgInventory(fgInventory);
    }

    /**
     * 修改成品库存
     *
     * @param fgInventory 成品库存
     * @return 结果
     */
    @Override
    public int updateFgInventory(FgInventory fgInventory)
    {
        return fgInventoryMapper.updateFgInventory(fgInventory);
    }

    /**
     * 批量删除成品库存
     *
     * @param ids 需要删除的成品库存主键
     * @return 结果
     */
    @Override
    public int deleteFgInventoryByIds(Long[] ids)
    {
        return fgInventoryMapper.deleteFgInventoryByIds(ids);
    }

    /**
     * 删除成品库存信息
     *
     * @param id 成品库存主键
     * @return 结果
     */
    @Override
    public int deleteFgInventoryById(Long id)
    {
        return fgInventoryMapper.deleteFgInventoryById(id);
    }

    @Override
    public int checkInventoty(String uid) {
        return fgInventoryMapper.checkInventoty(uid);
    }

    @Override
    public int updateQuantity(String uid, long qty) {
        return fgInventoryMapper.updateQuantity(uid, qty);
    }

    @Override
    public FgInventory getInventoryInfo(String uid) {

        FgInventory inventory = fgInventoryMapper.getInventoryInfo(uid);
        return inventory;
    }

    @Override
    public String bindInventory(Long[] ids) {

        String returnMessage = "";
        // 获取选中绑定的ID信息
        List<FgInventory> fgInventoryList = fgInventoryMapper.getInfoByID(ids);
        FgInventory fgInventory = new FgInventory();
        // 用于存放650数据
        List<FgInventory> fgInventoryList1 = new ArrayList<>();
        for (int i = 0;i < fgInventoryList.size();i++) {
            if (fgInventoryList.get(i).getBatch() == null || fgInventoryList.get(i).getBatch().equals("")) {
                fgInventoryList1.add(fgInventoryList.get(i));
            }
            if (fgInventoryList.get(i).getBatch() != null && !fgInventoryList.get(i).getBatch().equals("")) {
                BeanUtil.copyProperties(fgInventoryList.get(i), fgInventory);
            }
        }
        if (fgInventory.getPartnumber() == null || !fgInventory.getPartnumber().substring(0, 3).equals("660")) {
            return "请选择需要绑定的660";
        }
        System.out.println(fgInventory.toString());
        if (fgInventory.getPn650() != null && !fgInventory.getPn650().equals("") && fgInventory.getPn650().equals("650")) {
            return "改660PN已绑定过650";
        }
        // 是否选中4个650PN
        long count = fgInventoryList1.stream().filter(distinctByKey(FgInventory::getPn650)).count();
        System.out.println(count);
        if (count == 4) {
            // 分组统计每个650总数
            Map<String, Long> collect = fgInventoryList1.stream().collect(
                    Collectors.groupingBy(
                            FgInventory::getPn650,
                            Collectors.summingLong(FgInventory::getQuantity)
                    )
            );
            for (String key : collect.keySet()) {
                long sum = collect.get(key);
                if (sum != fgInventory.getQuantity()) {
                    return key + "总数:" + sum + " 不等于对应660打印数量:" + fgInventory.getQuantity();
                }
            }
            // 将partnumber设置为空字符 方便导出；将650批次设置为660批次方便知道改650绑定了哪个660或 660绑定了哪些650
            for (int i = 0;i < fgInventoryList1.size();i++) {
                fgInventoryList1.get(i).setPartnumber("");
                fgInventoryList1.get(i).setBatch(fgInventory.getBatch());
            }
            // 批量更新
            int n = fgInventoryMapper.updatePNAndBacth(fgInventoryList1);
            if (n > 0) {
                // 更新660的650pn字段为650（表示已绑定过）
                fgInventoryMapper.updatePNByUID(fgInventory.getUid());
                returnMessage = "绑定成功";
            } else {
                returnMessage = "绑定失败";
            }

        } else {
            return "请选择4个650绑定660";
        }

        return returnMessage;
    }
    public static  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
