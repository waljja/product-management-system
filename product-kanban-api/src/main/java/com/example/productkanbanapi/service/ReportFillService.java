package com.example.productkanbanapi.service;

import com.example.productkanbanapi.entity.RecReport;
import com.example.productkanbanapi.entity.StorageReport;

import java.util.List;

/**
 * ReportFillService
 *
 * @author 丁国钊
 * @date 2023-08-23-15:36
 */
public interface ReportFillService {

    /**
     * 获取成品库存报表数据
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 成品库存报表数据
     */
    List<StorageReport> getStockReportData(String startDate, String endDate);

    /**
     * 获取收货报表数据
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 收货报表数据
     */
    List<RecReport> getRecReportData(String startDate, String endDate);

}
