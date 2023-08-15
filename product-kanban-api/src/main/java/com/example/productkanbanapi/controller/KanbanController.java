package com.example.productkanbanapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.Inventory;
import com.example.productkanbanapi.entity.ProductStorage;
import com.example.productkanbanapi.service.KanbanService;
import com.ktg.common.result.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public CommonResult<Page<Inventory>> getProductData(@RequestParam(value = "current") int current,
                                                        @RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        Page<ProductStorage> storageList = kanbanService.getStorageList(current, startDate, endDate);
        return new CommonResult(200, "查询成功", storageList);
    }

}
