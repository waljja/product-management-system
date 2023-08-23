package com.ktg.mes.fg.mapper;

import java.util.List;

import com.ktg.common.annotation.DataSource;
import com.ktg.common.enums.DataSourceType;
import com.ktg.mes.fg.domain.FgChecklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 成品送检单Mapper接口
 * 
 * @author jtm
 * @date 2023-03-18
 */
@Mapper
@Repository
public interface FgChecklistMapper
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

    /**
     * 判断是否已生成过打印单，但没打印，避免重复插入
     * @param fgChecklist
     * @return int
     * */
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
     * 删除成品送检单
     *
     * @param id 成品送检单主键
     * @return 结果
     */
     int deleteFgChecklistById(Long id);

    /**
     * 批量删除成品送检单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteFgChecklistByIds(Long[] ids);

    /**
     * 检查当前条码类型下，对应的业务是否已经生成了条码
     * @param fgChecklist
     * @return FgChecklist
     */
     FgChecklist checkBarcodeUnique(FgChecklist fgChecklist);

    /**
     * 根据UID查询结果生成打印单
     * @param uid
     * @return List<FgChecklist>
     */
    List<FgChecklist> selectFgChecklistByUid(String[] uid);

    /**
     * 模糊查询PN
     * @param pnn
     * @return List<FgChecklist>
     */
    List<FgChecklist> selectPn(String pnn);

    /**
     * 根据具体PN查询是否有多个批次数据
     * @param pn
     * @return List<FgChecklist>
     */
    List<FgChecklist> selectOnePn(String pn);

    /**
     * 根据具体PN和批次查询数据
     * Mybatis多个参数查询需使用@Param获取前端控制器对应参数
     * @param pn
     * @return FgChecklist
     */
    FgChecklist selectOnePn1(@Param("pn") String pn, @Param("batch") String batch);

    /**
     * 切换数据源、查询拉别
     * @param wo
     * @return String
     */
    @DataSource(value = DataSourceType.SLAVE)
    String checkLine(String wo);

    @DataSource(value = DataSourceType.SLAVE2)
    String checkLine2(String wo);

    /**
     * 切换数据源、查询检验员
     * @param wo
     * @return String
     */
    @DataSource(value = DataSourceType.SLAVE)
    FgChecklist checkQasign(String wo);

    @DataSource(value = DataSourceType.SLAVE1)
    FgChecklist checkQasign2(String wo);

    /**
     * 查询QA结果
     * @param uid
     * @return FgChecklist
     */
    FgChecklist getQAresult(String uid);

    /**
     * 根据UID销毁前送检单
     * @param uid
     * @return int
     */
    int destroyUid(String uid);

    /**
     * 根据PN和启用状态获取所有已打印的数量
     * @param pn sap101
     * @return Long
     */
    Long getUidNo_Sum(@Param("pn") String pn,@Param("sap101") String sap101);

    FgChecklist getIdInfo(FgChecklist fgChecklist);

    FgChecklist getIdInfo2(FgChecklist fgChecklist);

    /**
     * 更新被拆分的成品单数量
     * @param uid uidNo
     * @return int
     */
    int updateBeforeUidNo(@Param("uid") String uid,@Param("uidNo") Long uidNo);

    /**
     *  查询数据是否存在(从SAP导入原始数据到成品送检单表）
     * @param sap101 pn
     * @return int
     */
    int checkinfo(@Param("sap101") String sap101,@Param("pn") String pn);

    /**
     *  更新QA检查结果
     * @param pn qaresult plant
     * @return int
     */
    int updateQAresult(@Param("pn") String pn,@Param("qaresult") int qaresult,@Param("plant") String plant, @Param("batch") String batch);

    /**
     *  根据uid查询打印数据
     * @param uid
     * @return FgChecklist
     */
    FgChecklist getPrintInfo(String uid);
}
