package com.example.productkanbanapi.service;

import com.example.productkanbanapi.entity.StockReport;

import java.util.Collection;
import java.util.List;

/**
 * ReportFillService
 *
 * @author 丁国钊
 * @date 2023-08-23-15:36
 */
public interface ReportFillService {

    /**
     * 获取成品入库看板报表
     *
     * @return 成品入库看板报表
     */
    List<StockReport> getStockReportData(String startDate, String endDate);

}
