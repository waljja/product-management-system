package com.ktg.mes.fg.mapper;

import com.ktg.mes.fg.domain.FgInventory;
import com.ktg.mes.fg.domain.FgTagsInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 成品上架清单Mapper接口
 *
 * @author JiangTingming
 * @date 2023-05-05
 */
@Mapper
@Repository
public interface FgInventoryMapper {
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
     * 删除成品库存
     *
     * @param id 成品库存主键
     * @return 结果
     */
     int deleteFgInventoryById(Long id);

    /**
     * 批量删除成品库存
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteFgInventoryByIds(Long[] ids);

    /**
     * 根据UID判断是否已经绑库
     *
     */
     int checkInventoty(String uid);

    /**
     * 拆分后更新原库存数量
     *
     */
     int updateQuantity(@Param("uid") String uid, @Param("qty") long qty);

    /**
     * 产生备货单后更新状态（已产生备货单状态）
     *
     */
     int updateStatusByUid(String uid);

     FgInventory getInventoryInfo(String uid);

     /** ---------------------------- 贴纸 -------------------------------- */

     /**
      * 查询贴纸信息
      * */
     List<FgTagsInventory> getTagsInventoryInfo(String uid);


    /******************************************** 650 绑定 660 *********************************************/
    /** 根据ID获取信息 */
    List<FgInventory> getInfoByID(Long[] ids);

    /** 有批量设置650对应660栏位为空，批次为660批次  */
    int updatePNAndBacth(List<FgInventory> list);

    int updatePNByUID(String uid);

    List<FgInventory> getCK00Info(String batch);

    int checkPN(@Param("pn") String pn, @Param("toNo") String toNo, @Param("po") String po);

    int updateQHQuantity(@Param("toNo") String toNo, @Param("pn") String pn, @Param("po") String po, @Param("Quantity") long Quantity);

    long checkQHQuantity(@Param("toNo") String toNo, @Param("pn") String pn, @Param("po") String po, @Param("Quantity") long Quantity);

    int updateQHStatus(@Param("toNo") String toNo, @Param("pn") String pn, @Param("po") String po, @Param("Quantity") long Quantity);

}
