package com.ktg.mes.fg.service;

import java.util.List;
import com.ktg.mes.fg.domain.FgShipmentinfoByhand;

/**
 * 【请填写功能名称】Service接口
 *
 * @author JiangTingming
 * @date 2023-05-17
 */
public interface FgShipmentinfoByhandService
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
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteFgShipmentinfoByhandByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteFgShipmentinfoByhandById(Long id);

    int updateStatusByid(Long id);
}