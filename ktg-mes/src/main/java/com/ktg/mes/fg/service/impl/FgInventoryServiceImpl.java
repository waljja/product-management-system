package com.ktg.mes.fg.service.impl;

import com.ktg.common.utils.DateUtils;
import com.ktg.mes.fg.domain.FgInventory;
import com.ktg.mes.fg.mapper.FgInventoryMapper;
import com.ktg.mes.fg.service.FgInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
