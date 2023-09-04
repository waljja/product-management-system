package com.example.productkanbanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.productkanbanapi.entity.Inventory;
import com.example.productkanbanapi.entity.RecReport;
import com.example.productkanbanapi.entity.StorageReport;
import com.example.productkanbanapi.entity.XtendMaterialtransactions;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.service.ReportFillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ReportFillServiceImpl
 *
 * @author 丁国钊
 * @date 2023-08-23-15:36
 */
@Service
public class ReportFillServiceImpl implements ReportFillService {

    @Autowired
    KanbanMapper kanbanMapper;

    @Override
    public List<StorageReport> getStockReportData(String startDate, String endDate) {
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        List<StorageReport> storageReportList;
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            // 根据日期范围查询，SQL Server 分页必须有 order 排序
            queryWrapper
                    .apply("create_time >= str_to_date('" + startDate + "', '%Y-%m-%d %H:%i:%s')")
                    .apply("create_time <= str_to_date('" + endDate + "', '%Y-%m-%d %H:%i:%s')")
                    .orderByDesc("create_time");
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        storageReportList = kanbanMapper.findInStock(queryWrapper);
        return storageReportList;
    }

    @Override
    public List<RecReport> getRecReportData(String startDate, String endDate) {
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        List<RecReport> recReportList;
        QueryWrapper<XtendMaterialtransactions> queryWrapper = new QueryWrapper<>();
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            // 根据日期范围查询，SQL Server 分页必须有 order 排序
            queryWrapper
                    .likeRight("UID", "FG")
                    .eq("TransactionType", "315")
                    .apply("CONVERT(VARCHAR(20), TransactionTime, 21) >= '" + startDate + "'")
                    .apply("CONVERT(VARCHAR(20), TransactionTime, 21) <= '" + endDate + "'")
                    .orderByDesc("TransactionTime");
        } else {
            queryWrapper
                    .likeRight("UID", "FG")
                    .eq("TransactionType", "315")
                    .orderByDesc("TransactionTime");
        }
        recReportList = kanbanMapper.findRec(queryWrapper);
        return recReportList;
    }

}
