package com.example.productkanbanapi.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.ProductNotInStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 查找 未入库成品 数据
     *
     * @param page
     *        分页设置
     * @param queryWrapper
     *        条件构造器
     * @return 未入库成品数据
     */
    @DS("sqlserver")
    Page<ProductNotInStorage> findNotInStock(Page page, @Param("ew") QueryWrapper queryWrapper);

    /**
     * 查找 成品入库 UID
     *
     * @param queryWrapper
     *        条件构造器
     * @return 成品库存UID
     */
    List<String> findWarehousedUid(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查找 未入库成品 数据
     *
     * @param page
     *        分页设置
     * @param queryWrapper
     *        条件构造器
     * @return 未入库成品数据
     */
    Page<ProductNotInStorage> findNotPutIn(Page page, @Param("ew") QueryWrapper queryWrapper);

}
