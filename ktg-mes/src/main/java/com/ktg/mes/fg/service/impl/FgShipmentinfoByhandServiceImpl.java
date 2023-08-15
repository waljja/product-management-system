package com.ktg.mes.fg.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ktg.mes.fg.mapper.FgShipmentinfoByhandMapper;
import com.ktg.mes.fg.domain.FgShipmentinfoByhand;
import com.ktg.mes.fg.service.FgShipmentinfoByhandService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author JiangTingming
 * @date 2023-05-17
 */
@Service
public class FgShipmentinfoByhandServiceImpl implements FgShipmentinfoByhandService {
    @Autowired
    private FgShipmentinfoByhandMapper fgShipmentinfoByhandMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public FgShipmentinfoByhand selectFgShipmentinfoByhandById(Long id) {
        return fgShipmentinfoByhandMapper.selectFgShipmentinfoByhandById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param fgShipmentinfoByhand 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<FgShipmentinfoByhand> selectFgShipmentinfoByhandList(FgShipmentinfoByhand fgShipmentinfoByhand) {
        return fgShipmentinfoByhandMapper.selectFgShipmentinfoByhandList(fgShipmentinfoByhand);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param fgShipmentinfoByhand 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertFgShipmentinfoByhand(FgShipmentinfoByhand fgShipmentinfoByhand) {
        return fgShipmentinfoByhandMapper.insertFgShipmentinfoByhand(fgShipmentinfoByhand);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param fgShipmentinfoByhand 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateFgShipmentinfoByhand(FgShipmentinfoByhand fgShipmentinfoByhand) {
        return fgShipmentinfoByhandMapper.updateFgShipmentinfoByhand(fgShipmentinfoByhand);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteFgShipmentinfoByhandByIds(Long[] ids) {
        return fgShipmentinfoByhandMapper.deleteFgShipmentinfoByhandByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteFgShipmentinfoByhandById(Long id) {
        return fgShipmentinfoByhandMapper.deleteFgShipmentinfoByhandById(id);
    }

    @Override
    public int updateStatusByid(Long id) {
        return fgShipmentinfoByhandMapper.updateStatusByid(id);
    }
}