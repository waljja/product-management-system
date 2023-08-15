package com.honortone.api.controller;

import com.honortone.api.common.Result;
import com.honortone.api.service.OutStockService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 出库接口
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-24
 */

@Slf4j
@Controller
@Api("成品出库接口")
@RequestMapping("/outstock")
public class OutStockController {

    @Autowired
    private OutStockService outStockService;

    @ResponseBody
    @PostMapping("/ot")
    public Result outStock(@RequestBody String cpno){

        String returnMessage = outStockService.outStock(cpno);

        return Result.success(returnMessage);
    }
}
