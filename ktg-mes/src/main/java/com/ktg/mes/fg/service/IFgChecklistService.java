package com.ktg.mes.fg.service;

import java.util.Date;
import java.util.List;
import com.ktg.mes.fg.domain.FgChecklist;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;

/**
 * 成品送检单Service接口
 * 
 * @author JiangTingming
 * @date 2023-03-18
 */
public interface IFgChecklistService
{

    /**
     * 查询成品送检单
     * 
     * @param id 成品送检单主键
     * @return 成品送检单
     */
     FgChecklist selectFgChecklistById(Long id);

    /**
     * 查询成品送检单列表
     * 
     * @param fgChecklist 成品送检单
     * @return 成品送检单集合
     */
     List<FgChecklist> selectFgChecklistList(FgChecklist fgChecklist);

     int checkInfonChcklist(FgChecklist fgChecklist);

    /**
     * 新增成品送检单
     * 
     * @param fgChecklist 成品送检单
     * @return 结果
     */
     int insertFgChecklist(FgChecklist fgChecklist);

    /**
     * 修改成品送检单
     * 
     * @param fgChecklist 成品送检单
     * @return 结果
     */
     int updateFgChecklist(FgChecklist fgChecklist);

    /**
     * 批量删除成品送检单
     * 
     * @param ids 需要删除的成品送检单主键集合
     * @return 结果
     */
     int deleteFgChecklistByIds(Long[] ids);

    /**
     * 删除成品送检单信息
     * 
     * @param id 成品送检单主键
     * @return 结果
     */
     int deleteFgChecklistById(Long id);

    /**
     * 根据条码类型和业务内容ID判断条码是否已存在
     * @param fgChecklist
     * @return
     */
     String checkBarcodeUnique(FgChecklist fgChecklist);

    /**
     * 根据条码记录生成实际的条码，返回对应的url地址
     * @param fgChecklist
     * @return
     */
     String generateBarcode(FgChecklist fgChecklist);

    /**
     * 根据UID查询结果生成打印单
     * @param uid
     * @return
     */
     List<FgChecklist> selectFgChecklistByUid(String[] uid);

    /**
     * 模糊查询PN
     * @param pnn
     * @return
     */
     List<FgChecklist> selectPn(String pnn);

    /**
     * 根据具体PN查询是否有多个批次数据
     * @param pn
     * @return
     */
     List<FgChecklist> selectOnePn(String pn);

    /**
     * 根据具体PN和批次查询数据
     * @param pn，batch
     * @return
     */
     FgChecklist selectOnePn1(String pn,String batch);

     FgChecklist getQAresult(String uid);

     int destroyUid(String uid);

    Long getUidNo_Sum(String pn,String sap101);

    int updateBeforeUidNo(String uid,Long uidNo);

    int updateQAresult(String pn,int qaresult,String plant, String batch);

    FgChecklist getPrintInfo(String uid);

    /************** 从控制层(controller)转到业务层的代码  *******************/
    String devanning_PMC() throws Exception;

    String generateTO_NO(Date shipmentDate) throws Exception;
}
