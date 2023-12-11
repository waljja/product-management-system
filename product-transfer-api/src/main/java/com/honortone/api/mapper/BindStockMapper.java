package com.honortone.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honortone.api.controller.dto.InventoryDto;
import com.honortone.api.entity.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BindStockMapper extends BaseMapper<Inventory> {

    @DS("slave_2")
    int checkUID(String uid);

    @DS("slave_2")
    int checkUID2(String uid);

    @DS("slave_2")
    List<Inventory> checkInfoByUid_1(String uid);

    @DS("slave_2")
    int toinsert(Inventory inventory);

    @DS("slave_2")
    int updateInventory(Inventory inventory);

    @DS("slave_2")
    int updateStock(Inventory inventory);

    @DS("slave_2")
    String getUidId(String uid);

    // 目前没用上
    List<Inventory> checkInfoByUid_2(String uid);

    @DS("slave_2")
    Inventory checkInventory(String uid);

    @DS("slave_2")
    long checktagsSum(String uid);

    @DS("slave_2")
    int checkTags(String clientBatch);

    @DS("slave_2")
    int checkBox(String cartonNo);

    @DS("slave_2")
    int insertTagsInventory(TagsInventory tagsInventory);

    @DS("slave_4")
    BoxInventory getBoxInfo(String cartonNo);

    @DS("slave_2")
    int insertBoxInventory(BoxInventory boxInventory);

    @DS("slave_2")
    long checkBoxQuantity(String uid);

    @DS("slave_2")
    int updateQuantity(@Param("uid") String uid, @Param("quantity") long quantity);
    @DS("slave_2")
    int firstInsert(Inventory inventory);

    @DS("slave_3")
    int checkInstock(CheckList checkList);
//    int checkInstock(CheckList checkList);

    @DS("slave_3")
    int checkInstock2(CheckList checkList);

    @DS("slave_2")
    CheckList checkOldUid(String uid);

    @DS("slave_2")
    int updateQuantityAndStatus(@Param("quantity") long quantity, @Param("uid") String uid);

    @DS("slave_2")
    ToList getUidInfo(String uid);

    @DS("slave_2")
    Inventory getInventoryInfo(ToList toList);

    @DS("slave_2")
    int deleteTolistInfo(String uid);

    @DS("slave_2")
    int updateTosStatus(@Param("toNo") String toNo, @Param("quantity") long quantity);

    /**
     * 绑定走货区
     * */
    @DS("slave_2")
    int updateTosStock(@Param("toNo") String toNo, @Param("stock") String stock);

    @DS("slave_2")
    String checkStatusByUid(String uid);

    /**
     * 获取走货信息
     * */
    @DS("slave_2")
    FgShipmentInfo getShipmentInfoBytoNo(String toNo);

    @DS("slave_2")
    int checkStatusTosQH(String toNo);

}
