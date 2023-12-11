package com.ktg.mes.fg.service;

import com.ktg.mes.fg.domain.FgInventory;

import java.util.List;

/**
 * 成品上架清单Service接口
 *
 * @author JiangTiangming
 * @date 2023-05-05
 */
public interface FgInventoryService {
    /**
     * 查询成品库存
     *
     * @param id 成品库存主键
     * @return 成品库存
     */
     FgInventory selectFgInventoryById(Long id);

    /**
     * 查询成品库存列表
     *
     * @param fgInventory 成品库存
     * @return 成品库存集合
     */
     List<FgInventory> selectFgInventoryList(FgInventory fgInventory);

    List<FgInventory> selectFgInventoryList2(FgInventory fgInventory);

    /**
     * 新增成品库存
     *
     * @param fgInventory 成品库存
     * @return 结果
     */
     int insertFgInventory(FgInventory fgInventory);

    /**
     * 修改成品库存
     *
     * @param fgInventory 成品库存
     * @return 结果
     */
     int updateFgInventory(FgInventory fgInventory);

    /**
     * 批量删除成品库存
     *
     * @param ids 需要删除的成品库存主键集合
     * @return 结果
     */
     int deleteFgInventoryByIds(Long[] ids);

    /**
     * 删除成品库存信息
     *
     * @param id 成品库存主键
     * @return 结果
     */
     int deleteFgInventoryById(Long id);

     int checkInventoty(String uid);

     int updateQuantity(String uid, long qty);

    FgInventory getInventoryInfo(String uid);

    /******************************************** 650 绑定 660 *********************************************/
    String bindInventory(Long[] ids);


}
