package com.honortone.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honortone.api.entity.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
@Repository
public interface GMterialsMapper extends BaseMapper<Inventory> {

    /**
     * 下载车牌号
     * */
    @DS("slave_2")
    List<String> downloadCarno(String date);

    /**
     * 根据车牌号下载走货单号
     * */
    @DS("slave_2")
    List<String> downloadShipmentno(@Param("carno") String carno, @Param("date") String date);

    /**
     * 根据走货编号下载TO单
     * */
    @DS("slave_2")
    List<String> downloadTos(String shipmentno);

    @DS("slave_2")
    List<String> downloadTos2(String shipmentno);

    /**
     * 根据TO单下载具体备货单信息
     * */
    @DS("slave_2")
    List<ToList> downloadTono(String tono);

    /**
     * 根据客户PN查询对应绑定的UID
     * */
    @DS("slave_2")
    List<TagsInventory> selectClientTag(@Param("clientPn") String clientPn, @Param("clientBatch") String clientBatch);

    @DS("slave_2")
    List<TagsInventory> selectClientTag2(@Param("clientPn") String clientPn, @Param("clientBatch") String clientBatch);

    /**
     * 根据绑定UID查询需要出库（备货数量）
     * */
    @DS("slave_2")
    ToList checkTolistUID(String uid);

    @DS("slave_2")
    List<TagsInventory> getTolistAndTagsInfo(@Param("toNo") String toNo, @Param("pn") String pn, @Param("Quantity") long Quantity);

    @DS("slave_2")
    int updateTagsInfo(@Param("tagsInventory1") TagsInventory tagsInventory1, @Param("tagsInventory2") TagsInventory tagsInventory2);

    @DS("slave_2")
    long getSumQuantity(String uid);

    @DS("slave_2")
    long getSumQuantity2(String uid);

    @DS("slave_2")
    int updateTagsStauts(@Param("clientPn") String clientPn, @Param("clientBatch") String clientBatch, @Param("quantity") long quantity);

    @DS("slave_2")
    List<TagsInventory> checkTagsByUID(String uid);

    @DS("slave_2")
    int updatetagsInfo1(List<TagsInventory> list);

    @DS("slave_2")
    String getSplitUID(String uid);

    @DS("slave_2")
    int batchInsertInventory(List<Inventory> list);

    @DS("slave_2")
    int updateTagsStautsAll(String uid);

    @DS("slave_2")
    int updateQuantityStauts(@Param("uid") String uid, @Param("uidNo") long uidNo, @Param("status") int status);

    @DS("slave_2")
    int checkTags(String uid);

    @DS("slave_2")
    List<ToList> downloadOrder();

    @DS("slave_2")
    Float checkQuantityByUid(String uid);

    /**
     * 根据UID查询库存表数据给库存下架表
     * */
    @DS("slave_2")
    Inventory getInventoryInfo(String cpno);

    @DS("slave_2")
    String checkTolistByUid(String uid);

    @DS("slave_2")
    String checkTolistInfo(Inventory inventory);

    @DS("slave_2")
    String checkDate(String uid);

    @DS("slave_2")
    int insertAndUpdate(@Param("uid") String uid, @Param("inventory") Inventory inventory);

    @DS("slave_2")
    int updateInventoryStatus(@Param("uid") String uid, @Param("status1") int status1, @Param("status2") int status2);

    /**
     * 将下架成品数据存到下架表
     * */
    @DS("slave_2")
    int insertInventoryOut(Inventory inventory);

    /**
     * 删除库存表中下架的成品数据
     * */
    @DS("slave_2")
    int deleteiinventoryByUid(String cpno);

    /**
     * 更新To明细表的备货单
     * */
    @DS("slave_2")
    int updateTono(String cpno);

    @DS("slave_2")
    int checkStauts(String uid);

    @DS("slave_2")
    int updateTosBHStatus(@Param("uid") String uid, @Param("Status") int Status);

    @DS("slave_2")
    int checkStauts2(String replaceUid);

    @DS("slave_2")
    int updateTosBHStatus2(@Param("replaceUid") String replaceUid, @Param("Status") int Status);

    List<Inventory> downloadorder();

    /**
     * 手动收货资料
     * */
    @DS("slave_2")
    List<ShipmentInfoByhand> downloadShipmentinfoByhand();

    @DS("slave_2")
    List<ShipmentInfoByhand> downloadShipmentinfoByhand2(String client);

    /**
     * 根据客户获取走货信息
     * */
    @DS("slave_2")
    List<ShipmentInfoByhand> getShipmentInfo(String client);

    /**
     * 单个确认
     * */
    @DS("slave_2")
    int updateByid(int id);

    /**
     * 一键确认（根据用户）
     * */
    @DS("slave_2")
    int updateAllByid(String client);

    /**
     * 查询备货单对应明细数量和已拣货数量
     * */
    @DS("slave_2")
    List<Map<Integer, Integer>> getQty(String tono);

    /**
     * 下载走货单 （排除有车号的走货单）
     * */
    @DS("slave_2")
    List<String> downloadShipmentNo2(String date);

    /**
     * 查询已手动备货数量和当前用户总数量
     * */
    @DS("slave_2")
    List<Map<Integer, Integer>> getQuantity(String client);

    /**
     * 获取同PN、PO的走货单数量
     * */
    @DS("slave_2")
    long getsumQty(FgShipmentInfo fgShipmentInfo);

    @DS("slave_2")
    int checkPnAndPo(FgShipmentInfo fgShipmentInfo);

    /**
     * 查询备货单或欠货单
     * */
    @DS("slave_2")
    FgTosAndTosListDto checkBHorQH(@Param("fgShipmentInfo") FgShipmentInfo fgShipmentInfo, @Param("Status") int Status);

    @DS("slave_2")
    List<FgTosAndTosListDto> getTosAndTOListInfo(FgShipmentInfo fgShipmentInfo);

    @DS("slave_2")
    int insertTos(FgTosAndTosListDto fgTosAndTosListDto);

    @DS("slave_2")
    int insertToList(FgTosAndTosListDto fgTosAndTosListDto);

    @DS("slave_2")
    int updateTosQuantity(FgTosAndTosListDto fgTosAndTosListDto);

    @DS("slave_2")
    int updateStatusByUid(String uid);

    @DS("slave_2")
    long getSumqty(String ShipmentNO);

    @DS("slave_2")
    ToList checkEqualQuantity(@Param("fgShipmentInfo") FgShipmentInfo fgShipmentInfo, @Param("qty") long qty, @Param("Status") int Status);


    @DS("slave_2")
    Inventory getInventoryInfo2(String uid);

    @DS("slave_2")
    int insertInventory(Inventory inventory);

    @DS("slave_2")
    int insertInventory2(List<Inventory> list);

    @DS("slave_2")
    int deleteToListtoNo2(String uid);

    @DS("slave_2")
    int deleteToListtoNo3(List<ToList> list);

    @DS("slave_2")
    long getQuantitySum(String toNo);

    @DS("slave_2")
    int updateTosListQuantity(@Param("toNo") String toNo, @Param("batch_qty") long batch_qty);

    @DS("slave_2")
    int updateTosListQuantity2(@Param("uid") String uid, @Param("quantity") long quantity);

    @DS("slave_2")
    ToList checkEqualQuantity2(@Param("fgShipmentInfo") FgShipmentInfo fgShipmentInfo, @Param("qty") long qty, @Param("Status") int Status);

    @DS("slave_2")
    List<ToList> checkEqualQuantity3(@Param("fgShipmentInfo") FgShipmentInfo fgShipmentInfo, @Param("qty") long qty, @Param("Status") int Status);

    @DS("slave_2")
    int updateStatusTos(String toNo);

    /** ****************************************   箱号下架   ************************************* **/
    BoxInventory selectBox(String cartonNo);

    List<BoxInventory> getTolistAndBoxInfo(@Param("toNo") String toNo, @Param("pn") String pn, @Param("Quantity") long Quantity);

    long getSumcartonQty(String uid);

    @DS("slave_2")
    int updateBoxInfo(@Param("boxInventory1") BoxInventory boxInventory1, @Param("boxInventory2") BoxInventory boxInventory2);

    @DS("slave_2")
    List<BoxInventory> checkBoxByUID(String uid);

    @DS("slave_2")
    int updateboxInfo1(List<BoxInventory> list);

    @DS("slave_2")
    int updateBoxStauts(String cartonNo);



    /**
     * 是否已拣货
     * */
    @DS("slave_2")
    List<String> checktoNo(FgShipmentInfo fgShipmentInfo);

    /**
     * 删除TOList中备货单/欠货单
     * */
    @DS("slave_2")
    int deleteToListtoNo(List<String> toNo);

    /**
     * 删除TOS中走货单产生的TO单（备货/欠货）
     * */
    @DS("slave_2")
    int deleteTostoNo(String shipmentNO);

    /**
     * 删除FgshipmentInfo中对应走货单信息
     * */
    @DS("slave_2")
    int deleteFgShipmentInfo(String shipmentNO);

    /**
     * 插入走货单信息用于生成新TO单
     * */
    @DS("slave_2")
    int insertShipmentInfo(List<FgShipmentInfo> fgShipmentInfoList);

    /**
     * 变更后删除走货信息
     * */
    @DS("slave_2")
    int deleteShipmentNo(String shipmentNO);

    /**
     * 重新插入走货信息
     * */
    @DS("slave_2")
    int deleteShipmentNo(List<com.ktg.mes.fg.domain.FgShipmentInfo> list);

    /*******************************************     CK00     **********************************************/
    @DS("slave_2")
    String checkCk00(String toNo);

    @DS("slave_2")
    List<ToList> downloadTonoCK00(String toNo);

    @DS("slave_2")
    List<Map<Integer, Integer>> getQtyCK00(String tono);

}
