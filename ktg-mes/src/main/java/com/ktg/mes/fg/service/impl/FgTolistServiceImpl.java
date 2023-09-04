package com.ktg.mes.fg.service.impl;

import com.ktg.mes.fg.domain.FgToList;
import com.ktg.mes.fg.mapper.FgTolistMapper;
import com.ktg.mes.fg.service.FgTolistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成品备货清单Service业务层处理
 *
 * @author JiangTingming
 * @date 2023-05-05
 */
@Service
public class FgTolistServiceImpl implements FgTolistService {
    @Autowired
    private FgTolistMapper fgTolistMapper;

    @Override
    public long getReadyBH() {
        return fgTolistMapper.getReadyBH();
    }

    @Override
    public long getBHing() {
        return fgTolistMapper.getBHing();
    }

    /**
     * 查询TO明细
     *
     * @param id TO明细主键
     * @return TO明细
     */
    @Override
    public FgToList selectFgTolistById(Long id)
    {
        return fgTolistMapper.selectFgTolistById(id);
    }

    /**
     * 查询TO明细列表
     *
     * @param fgTolist TO明细
     * @return TO明细
     */
    @Override
    public List<FgToList> selectFgTolistList(FgToList fgTolist)
    {
        if (fgTolist.getStatus() != null && fgTolist.getStatus() == -1) {
            return fgTolistMapper.getAreaStockInfo();
        } else {
            return fgTolistMapper.selectFgTolistList(fgTolist);
        }

    }

    @Override
    public List<FgToList> selectFgTolistList2(FgToList fgToList) {
        return fgTolistMapper.selectFgTolistList2(fgToList);
    }

    /**
     * 新增TO明细
     *
     * @param fgTolist TO明细
     * @return 结果
     */
    @Override
    public int insertFgTolist(FgToList fgTolist)
    {
        return fgTolistMapper.insertFgTolist(fgTolist);
    }

    /**
     * 修改TO明细
     *
     * @param fgTolist TO明细
     * @return 结果
     */
    @Override
    public int updateFgTolist(FgToList fgTolist)
    {
        return fgTolistMapper.updateFgTolist(fgTolist);
    }

    /**
     * 批量删除TO明细
     *
     * @param ids 需要删除的TO明细主键
     * @return 结果
     */
    @Override
    public int deleteFgTolistByIds(Long[] ids)
    {
        return fgTolistMapper.deleteFgTolistByIds(ids);
    }

    /**
     * 删除TO明细信息
     *
     * @param id TO明细主键
     * @return 结果
     */
    @Override
    public int deleteFgTolistById(Long id)
    {
        return fgTolistMapper.deleteFgTolistById(id);
    }
}
