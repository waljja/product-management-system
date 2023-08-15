package com.example.productkanbanapi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.ProductStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * KanbanMapper
 *
 * @author 丁国钊
 * @date 2023-08-15-9:08
 */
@Mapper
@Repository
public interface KanbanMapper {

    /**
     * 查找 成品入库 数据
     *
     * @param page
     *        分页设置
     * @param queryWrapper
     *        条件构造器
     * @return 成品库存数据
     */
    Page<ProductStorage> findInventory(Page page, @Param("ew") QueryWrapper queryWrapper);

}
