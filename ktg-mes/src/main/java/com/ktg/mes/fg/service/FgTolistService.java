package com.ktg.mes.fg.service;

import com.ktg.mes.fg.domain.FgToList;
import java.util.List;

/**
 * 成品备货清单Service接口
 *
 * @author JiangTiangming
 * @date 2023-05-05
 */

public interface FgTolistService {

    long getReadyBH();

    long getBHing();

    /**
     * 查询TO明细
     *
     * @param id TO明细主键
     * @return TO明细
     */
     FgToList selectFgTolistById(Long id);

    /**
     * 查询TO明细列表
     *
     * @param fgTolist TO明细
     * @return TO明细集合
     */
     List<FgToList> selectFgTolistList(FgToList fgTolist);

    List<FgToList> selectFgTolistList2(FgToList fgToList);

    /**
     * 新增TO明细
     *
     * @param fgTolist TO明细
     * @return 结果
     */
     int insertFgTolist(FgToList fgTolist);

    /**
     * 修改TO明细
     *
     * @param fgTolist TO明细
     * @return 结果
     */
     int updateFgTolist(FgToList fgTolist);

    /**
     * 批量删除TO明细
     *
     * @param ids 需要删除的TO明细主键集合
     * @return 结果
     */
     int deleteFgTolistByIds(Long[] ids);

    /**
     * 删除TO明细信息
     *
     * @param id TO明细主键
     * @return 结果
     */
     int deleteFgTolistById(Long id);
}
