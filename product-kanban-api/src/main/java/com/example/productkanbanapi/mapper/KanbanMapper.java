package com.example.productkanbanapi.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.*;
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
     * 查找未入库成品数据（分页）
     *
     * @param page         分页配置
     * @param queryWrapper 条件构造器
     * @return 未入库成品数据
     */
    @DS("sqlserver")
    Page<NotInStorage> findNotInStock(Page page, @Param("ew") QueryWrapper queryWrapper);

    /**
     * 获取成品待入库数据
     *
     * @return 成品待入库数据
     */
    @DS("sqlserver")
    List<NotInStorage> findAllNotInStock();

    /**
     * 查找 未入库成品 数据
     *
     * @param page         分页配置
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 未入库成品数据
     */
    @DS("sqlserver")
    Page<NotInStorage> findNotInStockU(Page page, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("pns") List<String> pns);

    /**
     * 查找3天前开始的成品送检单数据
     *
     * @param queryWrapper 条件构造器
     * @return 3天前开始的成品送检单数据
     */
    List<NotInStorage> findPdsInfoFrom3DaysAgo(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查找 成品入库 UID
     *
     * @param queryWrapper 条件构造器
     * @return 成品库存UID
     */
    List<String> findWarehousedUid(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查找 库存 数据
     *
     * @param queryWrapper 条件构造器
     * @return 库存数据
     */
    List<StorageReport> findInStock(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查找 收货 数据
     *
     * @param queryWrapper 条件构造器
     * @return 收货数据
     */
    @DS("sqlserver")
    List<RecReport> findRec(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查找 成品出货 数据
     *
     * @param page         分页配置
     * @param queryWrapper 条件构造器
     * @return 成品出货数据
     */
    Page<Shipment> findShipment(Page page, @Param("ew") QueryWrapper queryWrapper);

    /**
     * 根据 出货单号 查询 零部件号
     *
     * @param queryWrapper 条件构造器
     * @return 一张出货单的所有零部件号
     */
    List<String> findPnByShipNo(@Param("ew") QueryWrapper<TosShipInfo> queryWrapper);

    /**
     * 确认UID是否过账
     *
     * @param queryWrapper 条件构造器
     * @return 是/否
     */
    @DS("sqlserver")
    String findTransByUid(@Param("ew") QueryWrapper<XtendMaterialtransactions> queryWrapper);

    /**
     * 删除所有转运未收货数据
     *
     * @return 成功/失败
     */
    @DS("sqlserver")
    int delAll313NotRec();

}
