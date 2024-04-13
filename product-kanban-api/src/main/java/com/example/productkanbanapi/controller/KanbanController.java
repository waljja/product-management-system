package com.example.productkanbanapi.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.*;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.result.CommonResult;
import com.example.productkanbanapi.service.KanbanService;
import com.example.productkanbanapi.service.ReportFillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    KanbanMapper kanbanMapper;

    /**
     * 获取成品入库看板数据（分页）
     *
     * @param current   当前页码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 成品入库数据
     */
    @ResponseBody
    @ApiOperation("获取成品入库看板数据（分页）")
    @GetMapping("/product-storage/get-data")
    public CommonResult<Page<Inventory>> getStockKanbanData(@RequestParam(value = "current") int current,
                                                            @RequestParam(value = "startDate", required = false) String startDate,
                                                            @RequestParam(value = "endDate", required = false) String endDate,
                                                            @RequestParam(value = "pns", required = false) String[] pnArr,
                                                            @RequestParam(value = "states", required = false) String[] stateArr,
                                                            @RequestParam(value = "wos", required = false) String[] woArr) {
        Page<NotInStorage> storageList = kanbanService.getStorageList(current, startDate, endDate,
                pnArr != null ? Arrays.stream(pnArr).collect(Collectors.toList()) : new ArrayList<>(),
                stateArr != null ? Arrays.stream(stateArr).collect(Collectors.toList()) : new ArrayList<>(),
                woArr != null ? Arrays.stream(woArr).collect(Collectors.toList()) : new ArrayList<>());
        return new CommonResult(200, "查询成功", storageList);
    }

    /**
     * 获取成品入库看板数据
     *
     * @param current   当前页码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 成品入库数据
     */
    @ResponseBody
    @ApiOperation("获取成品入库看板数据")
    @GetMapping("/product-storage/get-all")
    public CommonResult<List<Inventory>> getAllStockData() {
        List<NotInStorage> storageList = kanbanMapper.findAllNotInStock();
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
    @ApiOperation("获取出货计划看板数据")
    @GetMapping("/product-shipment/get-data")
    public CommonResult<Page<Inventory>> getShipmentKanbanData(@RequestParam(value = "current") int current,
                                                               @RequestParam(value = "startDate", required = false) String startDate,
                                                               @RequestParam(value = "endDate", required = false) String endDate) {
        Page<Shipment> shipmentList = kanbanService.getShipmentList(current, startDate, endDate);
        return new CommonResult(200, "查询成功", shipmentList);
    }

    /**
     * 入库报表下载
     *
     * @return 入库报表
     */
    @ResponseBody
    @ApiOperation("入库报表下载")
    @GetMapping(path = "/stock-report/download")
    public void downloadStockReport(HttpServletResponse response,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate) throws IOException {
        // 报表文件名称
        String fileName = URLEncoder
                .encode("成品入库看板报表", "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "收货报表").head(RecReport.class).build();
            WriteSheet writeSheet2 = EasyExcel.writerSheet(1, "成品库存报表").head(StorageReport.class).build();
            excelWriter.write(reportFillService.getRecReportData(startDate, endDate), writeSheet1);
            excelWriter.write(reportFillService.getStockReportData(startDate, endDate), writeSheet2);
            excelWriter.finish();
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }
    }

}
