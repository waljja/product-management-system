package com.ktg.mes.fg.service.impl;

import com.ktg.mes.fg.domain.FgUnpacking;
import com.ktg.mes.fg.mapper.FgUnpackingMapper;
import com.ktg.mes.fg.service.FgUnpackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成品拆箱清单Service业务层处理
 *
 * @author JiangTingming
 * @date 2023-05-05
 */
@Service
public class FgUnpackingServiceImpl implements FgUnpackingService {
    @Autowired
    private FgUnpackingMapper fgUnpackingMapper;

    /**
     * 查询拆分明细
     *
     * @param id 拆分明细主键
     * @return 拆分明细
     */
    @Override
    public FgUnpacking selectFgUnpackingById(Long id)
    {
        return fgUnpackingMapper.selectFgUnpackingById(id);
    }

    /**
     * 查询拆分明细列表
     *
     * @param fgUnpacking 拆分明细
     * @return 拆分明细
     */
    @Override
    public List<FgUnpacking> selectFgUnpackingList(FgUnpacking fgUnpacking)
    {
        System.out.println("1:"+fgUnpackingMapper.selectFgUnpackingList(fgUnpacking));
        return fgUnpackingMapper.selectFgUnpackingList(fgUnpacking);
    }

    /**
     * 新增拆分明细
     *
     * @param fgUnpacking 拆分明细
     * @return 结果
     */
    @Override
    public int insertFgUnpacking(FgUnpacking fgUnpacking)
    {
        return fgUnpackingMapper.insertFgUnpacking(fgUnpacking);
    }

    /**
     * 修改拆分明细
     *
     * @param fgUnpacking 拆分明细
     * @return 结果
     */
    @Override
    public int updateFgUnpacking(FgUnpacking fgUnpacking)
    {
        return fgUnpackingMapper.updateFgUnpacking(fgUnpacking);
    }

    /**
     * 批量删除拆分明细
     *
     * @param ids 需要删除的拆分明细主键
     * @return 结果
     */
    @Override
    public int deleteFgUnpackingByIds(Long[] ids)
    {
        return fgUnpackingMapper.deleteFgUnpackingByIds(ids);
    }

    /**
     * 删除拆分明细信息
     *
     * @param id 拆分明细主键
     * @return 结果
     */
    @Override
    public int deleteFgUnpackingById(Long id)
    {
        return fgUnpackingMapper.deleteFgUnpackingById(id);
    }
}
