package com.ktg.mes.fg.mapper;

import com.ktg.mes.fg.domain.FgUnpacking;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 成品拆箱清单Mapper接口
 *
 * @author JiangTingming
 * @date 2023-05-05
 */
@Mapper
@Repository
public interface FgUnpackingMapper {
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
     * 删除拆分明细
     *
     * @param id 拆分明细主键
     * @return 结果
     */
     int deleteFgUnpackingById(Long id);

    /**
     * 批量删除拆分明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteFgUnpackingByIds(Long[] ids);
}
