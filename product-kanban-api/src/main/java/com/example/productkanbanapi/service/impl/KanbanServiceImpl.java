package com.example.productkanbanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.NotInStorage;
import com.example.productkanbanapi.entity.Shipment;
import com.example.productkanbanapi.entity.XtendMaterialtransactions;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.service.KanbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public Page<NotInStorage> getStorageList(int current, String startDate, String endDate) {
        Page<NotInStorage> productNotInStoragePage;
        List<String> warehousedUid;
        // current -> 当前页码，每页 20 条数据
        Page inventoryPage = new Page(current, 20);
        QueryWrapper<XtendMaterialtransactions> transQueryWrapper1 = new QueryWrapper<>();
        QueryWrapper<XtendMaterialtransactions> transQueryWrapper2 = new QueryWrapper<>();
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        // 已入库UID
        warehousedUid = kanbanMapper.findWarehousedUid(transQueryWrapper1);
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            // 根据日期范围查询，SQL Server 分页必须有 order 排序
            transQueryWrapper2
                    .likeRight("UID", "FG")
                    .notIn("UID", warehousedUid)
                    .eq("TransactionType", "315")
                    .apply("CONVERT(VARCHAR(20), TransactionTime, 21) >= '" + startDate + "'")
                    .apply("CONVERT(VARCHAR(20), TransactionTime, 21) <= '" + endDate + "'")
                    .orderByAsc("TransactionTime");
        } else {
            transQueryWrapper2
                    .likeRight("UID", "FG")
                    .notIn("UID", warehousedUid)
                    .eq("TransactionType", "315")
                    .orderByAsc("TransactionTime");
        }
        productNotInStoragePage = kanbanMapper.findNotInStock(inventoryPage, transQueryWrapper2);
        return productNotInStoragePage;
    }

    @Override
    public Page<Shipment> getShipmentList(int current, String startDate, String endDate) {
        Page<Shipment> shipmentPage;
        List<String> WarehousedUid;
        // current -> 当前页码，每页 20 条数据
        Page inventoryPage = new Page(current, 20);
        QueryWrapper<XtendMaterialtransactions> transQueryWrapper1 = new QueryWrapper<>();
        QueryWrapper<XtendMaterialtransactions> transQueryWrapper2 = new QueryWrapper<>();
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        // 已入库UID
        WarehousedUid = kanbanMapper.findWarehousedUid(transQueryWrapper1);
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            // 根据日期范围查询，SQL Server 分页必须有 order 排序
            transQueryWrapper2
                    .likeRight("UID", "FG")
                    .notIn("UID", WarehousedUid)
                    .eq("TransactionType", "315")
                    .apply("CONVERT(VARCHAR(20), TransactionTime, 21) >= '" + startDate + "'")
                    .apply("CONVERT(VARCHAR(20), TransactionTime, 21) <= '" + endDate + "'")
                    .orderByAsc("TransactionTime");
        } else {
            transQueryWrapper2
                    .likeRight("UID", "FG")
                    .notIn("UID", WarehousedUid)
                    .eq("TransactionType", "315")
                    .orderByAsc("TransactionTime");
        }
        shipmentPage = kanbanMapper.findShipment(inventoryPage, transQueryWrapper2);
        return shipmentPage;
    }

}
