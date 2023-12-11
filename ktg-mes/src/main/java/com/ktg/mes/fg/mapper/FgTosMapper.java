package com.ktg.mes.fg.mapper;

import cn.hutool.core.map.TolerantMap;
import com.ktg.mes.fg.domain.*;
import com.ktg.mes.fg.domain.Dto.FgTosAndTosListDto;
import com.ktg.mes.fg.domain.Dto.ReturnResult;
import com.ktg.mes.fg.domain.Dto.ShipmentPart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TO管理Mapper接口
 * 
 * @author JiangTingming
 * @date 2023-04-18
 */
@Mapper
@Repository
public interface FgTosMapper
{

    /**
     * 查询从SAP导入的走货资料
     * @return
     */
    List<FgShipmentInfo> selectFgShipmentInfoList();

    /**
     * 将走货资料导入数据库
     * @param fgShipmentInfo
     * @return
     */
    int insertFgShipmentInfo(FgShipmentInfo fgShipmentInfo);

    List<FgShipmentInfo> selectFgShipment();

    int updateFgShipmentStatus(FgShipmentInfo fgShipmentInfo);

    /**
     * 根据PN和PO、最后确认部门查询是否存在
     * @param --SapPn po lastcomfirm
     * @return int
     */
    int checkInfoFs(FgShipmentInfo fgShipmentInfo);

    int checkInfoFs2(FgShipmentInfo fgShipmentInfo);

    int checkInfoFs3(FgShipmentInfo fgShipmentInfo);

    int updateShipmentQuantity(FgShipmentInfo fgShipmentInfo);

    /**
     * 关联存库表、SAP走货信息整合数据ShipmentPart获取To明细等数据
     * @param pn po lastcomfirm
     * @return List<FgTosAndTosListDto>
     */
    List<FgTosAndTosListDto> getTosAndTOListInfo(@Param("ShipmentNO") String ShipmentNO, @Param("pn") String pn, @Param("po") String po,@Param("lastcomfirm") String lastcomfirm);

    long gettoNoSum(String toNo);

    int updateTosQuantity(@Param("quantity") long quantity, @Param("toNo") String toNo);

    int updateTolistQuantity(@Param("quantity") long quantity, @Param("toNo") String toNo);

    /**
     * 拆箱明细表
     * @param fgUnpacking
     * @return int
     */
    int insertFgUnpacking(FgUnpacking fgUnpacking);

    /**
     * 拆箱/产生备货单后更新状态，避免重复获取
     * @param pn po lastcomfirm
     * @return int
     */
    int updatePMCstatus(@Param("pn") String pn,@Param("po") String po,@Param("lastcomfirm") String lastcomfirm, @Param("ShipmentNO") String ShipmentNO);

    /**
     * 插入TO管理前查重
     * @param ShipmentNo
     * @return int
     */
    int checkInfoTos(@Param("BH") String BH, @Param("ShipmentNo") String ShipmentNo);
    //int checkInfoTos(@Param("pn") String pn,@Param("po") String po,@Param("ShipmentNo") String ShipmentNo);

    /**
     * 将数据插入TO管理表
     * @param fgTos
     * @return
     */
    int insertFgTos(FgTos fgTos);

    int insertFgTolist(FgToList fgToList);

    /**
     * 用于拆箱\产生备货/欠货单（后续关联库存表）
     * @param lastcomfirm
     * @return List<ShipmentPart>
     */
    List<ShipmentPart> selectShipmentPart(String lastcomfirm);

    List<ShipmentPart> selectShipmentPartCK00(String lastcomfirm);

    /**
     * 根据走货编号获取走货数量
     */
    long getQuantityByshipmentno(String ShipmentNO);

    /**
     * 根据走货编号查询是否已产生备货单
     */
    String checkTosByshipmentno_BH(String ShipmentNO);

    /**
     * 根据走货编号查询是否已产生欠货单
     */
    String checkTosByshipmentno_QH(String ShipmentNO);

    /**
     * 根据走货单和明细状态查询欠货总数
     */
    long getSumqty(String shipmentNO);

    /**
     * 根据走货单、状态 更新TO管理的欠货单数量
     */
    int updateQuantity(@Param("sumqty") long sumqty, @Param("shipmentNO") String shipmentNO);

    int updateQuantity2(@Param("sumqty") long sumqty, @Param("toNo") String toNo);

    /**
     * 获取欠货单集合
     * */
    List<FgTos> getQHList();

    List<FgTos> getQHList2();

    List<FgToList> getToListByQH(String toNo);

    int updateTosQH(@Param("toNo") String toNo, @Param("shipmentNo") String shipemntNo);

    FgInventory checkInventory(FgToList fgToList);

    List<FgInventory> checkInventory2(FgToList fgToList);

    FgTos checkBHstatus(String shipmentNo);

    int sumBH(@Param("quantity") long quantity, @Param("toNo") String toNo);

    int updatebatchSum(@Param("quantity") long quantity, @Param("toNo") String toNo);

    int updateQHStatus(FgToList fgToList);

    int updateQHQuantuty(@Param("fgToList") FgToList fgToList, @Param("Quantity2") long Quantity2);

    int updateTosQHQuantuty(@Param("Quantity") long Quantity, @Param("toNo") String toNo);

    long getQHlastQuantity(String toNo);

    long getBHlastQuantity(String toNo);

    int updateQHlastQuantuty(@Param("Quantity") long Quantity, @Param("toNo") String toNo);

    int updateInventoryStatus(String uid);

    FgToList getTolistInfo(String uid);


    /**
     * 查询当天起到后两天的走货编号
     * */
    List<FgShipmentInfo> getShipmentInfo(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 批量更新走货日期
     * */
    int updateShipmentDate(List<FgShipmentInfo> list);

    List<String> getTOUID(String shipmentNO);

    int updateInventoryBYuid(List<String> list);

    int deleteTOinfo(String shipmentNO);

    int deleteTOinfo2(String shipmentNO);

    int updateShipmentInfoStatus(String shipmentNO);

    int updateTOlistBYuid(String shipemntNO);

    int updateTOSBYshipmentNO(String shipmentNO);

    /*********************************************   CK00 TO    ****************************************************/
    /** 查询660对应是否都对应4个650 */
    List<String> checkCount(ShipmentPart shipmentPart);

    ReturnResult checkSumByWO(@Param("pn6501") String pn6501, @Param("pn6502") String pn6502, @Param("pn6503") String pn6503, @Param("pn6504") String pn6504, @Param("pn") String pn, @Param("po") String po, @Param("ShipmentSum") long ShipmentSum);

    List<ReturnResult> getInfo650(@Param("pn650") String pn650, @Param("pn") String pn, @Param("po") String po, @Param("quantity") long quantity, @Param("wo") String wo);

}
