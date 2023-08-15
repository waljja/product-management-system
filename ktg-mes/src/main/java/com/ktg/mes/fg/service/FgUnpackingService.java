package com.ktg.mes.fg.service;

import com.ktg.mes.fg.domain.FgUnpacking;

import java.util.List;

/**
 * 成品拆箱清单Service接口
 *
 * @author JiangTiangming
 * @date 2023-05-05
 */
public interface FgUnpackingService {
    /**
     * 查询拆分明细
     *
     * @param id 拆分明细主键
     * @return 拆分明细
     */
     FgUnpacking selectFgUnpackingById(Long id);

    /**
     * 查询拆分明细列表
     *
     * @param fgUnpacking 拆分明细
     * @return 拆分明细集合
     */
     List<FgUnpacking> selectFgUnpackingList(FgUnpacking fgUnpacking);

    /**
     * 新增拆分明细
     *
     * @param fgUnpacking 拆分明细
     * @return 结果
     */
     int insertFgUnpacking(FgUnpacking fgUnpacking);

    /**
     * 修改拆分明细
     *
     * @param fgUnpacking 拆分明细
     * @return 结果
     */
     int updateFgUnpacking(FgUnpacking fgUnpacking);

    /**
     * 批量删除拆分明细
     *
     * @param ids 需要删除的拆分明细主键集合
     * @return 结果
     */
     int deleteFgUnpackingByIds(Long[] ids);

    /**
     * 删除拆分明细信息
     *
     * @param id 拆分明细主键
     * @return 结果
     */
     int deleteFgUnpackingById(Long id);
}
