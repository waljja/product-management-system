package com.example.productkanbanapi.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.Inventory;
import com.example.productkanbanapi.entity.NotInStorage;
import com.example.productkanbanapi.entity.StockReport;
import com.example.productkanbanapi.result.CommonResult;
import com.example.productkanbanapi.service.KanbanService;
import com.example.productkanbanapi.service.ReportFillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * KanbanController
 *
 * @author 丁国钊
 * @date 2023-08-14-16:54
 */
@Slf4j
@Controller
@Api("成品入库看板接口")
@RequestMapping(value = "/kanban")
public class KanbanController {

    @Autowired
    KanbanService kanbanService;
    @Autowired
    ReportFillService reportFillService;

    /**
     * 获取成品入库看板数据
     *
     * @param current   当前页码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 成品入库数据
     */
    @ResponseBody
    @GetMapping("/product-storage/get-data")
    public CommonResult<Page<Inventory>> getStockKanbanData(@RequestParam(value = "current") int current,
                                                            @RequestParam(value = "startDate", required = false) String startDate,
                                                            @RequestParam(value = "endDate", required = false) String endDate) {
        Page<NotInStorage> storageList = kanbanService.getStorageList(current, startDate, endDate);
        return new CommonResult(200, "查询成功", storageList);
    }

    /**
     * 获取出货计划看板数据
     *
     * @param current   当前页码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 出货计划看板数据
     */
    @ResponseBody
    @GetMapping("/product-shipment/get-data")
    public CommonResult<Page<Inventory>> getShipmentKanbanData(@RequestParam(value = "current") int current,
                                                               @RequestParam(value = "startDate", required = false) String startDate,
                                                               @RequestParam(value = "endDate", required = false) String endDate) {
        Page<NotInStorage> storageList = kanbanService.getStorageList(current, startDate, endDate);
        return new CommonResult(200, "查询成功", storageList);
    }

    /**
     * 入库报表下载
     *
     * @return 入库报表
     */
    @ResponseBody
    @GetMapping(path = "/stock-report/download")
    public void downloadStockReport(HttpServletResponse response,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate) throws IOException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 报表文件名称
        String fileName = URLEncoder
                .encode("成品入库报表(" + format.format(date) + ")", "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        EasyExcel
                .write(response.getOutputStream(), StockReport.class)
                .sheet("成品入库报表")
                .doWrite(reportFillService.getStockReportData(startDate, endDate));
    }

}
