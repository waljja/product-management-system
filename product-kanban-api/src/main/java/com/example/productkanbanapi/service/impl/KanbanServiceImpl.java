package com.example.productkanbanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.Inventory;
import com.example.productkanbanapi.entity.ProductStorage;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.service.KanbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * KanbanServiceImpl
 *
 * @author 丁国钊
 * @date 2023-08-14-16:57
 */
@Service
public class KanbanServiceImpl implements KanbanService {

    @Autowired
    KanbanMapper kanbanMapper;

    @Override
    public Page<ProductStorage> getStorageList(int current, String startDate, String endDate) {
        Page<ProductStorage> productStoragePage;
        // current -> 当前页码，每页 20 条数据
        Page inventoryPage = new Page(current, 20);
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            // 根据日期范围查询，SQL Server 分页必须有 order 排序
            queryWrapper
                    .apply("CONVERT(VARCHAR(20), CreateDate,21) >= '" + startDate + "'")
                    .apply("CONVERT(VARCHAR(20), CreateDate,21) <= '" + endDate + "'")
                    .orderByAsc("CreateDate");
        } else {
            queryWrapper.orderByAsc("CreateDate");
        }
        productStoragePage = kanbanMapper.findInventory(inventoryPage, queryWrapper);
        return productStoragePage;
    }

}
