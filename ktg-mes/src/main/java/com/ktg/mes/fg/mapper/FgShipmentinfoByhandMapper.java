package com.ktg.mes.fg.mapper;

import java.util.List;

import com.ktg.mes.fg.domain.FgSealnoinfo;
import com.ktg.mes.fg.domain.FgShipmentinfoByhand;
import io.lettuce.core.dynamic.annotation.Param;

import javax.xml.crypto.Data;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author JiangTingming
 * @date 2023-05-17
 */
public interface FgShipmentinfoByhandMapper
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public FgShipmentinfoByhand selectFgShipmentinfoByhandById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param fgShipmentinfoByhand 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<FgShipmentinfoByhand> selectFgShipmentinfoByhandList(FgShipmentinfoByhand fgShipmentinfoByhand);

    /**
     * 新增【请填写功能名称】
     *
     * @param fgShipmentinfoByhand 【请填写功能名称】
     * @return 结果
     */
    public int insertFgShipmentinfoByhand(FgShipmentinfoByhand fgShipmentinfoByhand);

    /**
     * 修改【请填写功能名称】
     *
     * @param fgShipmentinfoByhand 【请填写功能名称】
     * @return 结果
     */
    public int updateFgShipmentinfoByhand(FgShipmentinfoByhand fgShipmentinfoByhand);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteFgShipmentinfoByhandById(Long id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFgShipmentinfoByhandByIds(Long[] ids);

    /**
     * 查重
     *
     * @param fgShipmentinfoByhand
     * @return 结果
     */
    int getData(FgShipmentinfoByhand fgShipmentinfoByhand);

    int getData1(FgSealnoinfo fgSealnoinfo);

    /**
     * 批量插入
     *
     * @param list
     * @return 结果
     */
    int batchInsert(List<FgShipmentinfoByhand> list);

    int batchInsert1(List<FgSealnoinfo> list);

    /**
     * 更新确认状态
     *
     * @param id
     * @return 结果
     */
    int updateStatusByid(Long id);

    /**************************** 维护封条号 **********************************/

    List<FgSealnoinfo> selectFgSealnoinfoList(FgSealnoinfo fgSealnoinfo);

    int deleteFgSealnoinfoByIds(Long[] ids);
}