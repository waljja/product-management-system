package com.example.productkanbanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.productkanbanapi.entity.StockReport;
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
    public List<StockReport> getStockReportData(String startDate, String endDate) {
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        List<String> warehousedUid;
        List<StockReport> stockReportList;
        QueryWrapper<XtendMaterialtransactions> transQueryWrapper1 = new QueryWrapper<>();
        QueryWrapper<XtendMaterialtransactions> transQueryWrapper2 = new QueryWrapper<>();
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
        stockReportList = kanbanMapper.findStockKanbanData(transQueryWrapper2);
        return stockReportList;
    }

}
