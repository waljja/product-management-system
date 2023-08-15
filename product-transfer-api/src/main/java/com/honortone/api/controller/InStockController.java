package com.honortone.api.controller;

import cn.hutool.json.JSONObject;
import com.honortone.api.common.Result;
import com.honortone.api.controller.dto.MaterialTransationsDto;
import com.honortone.api.service.InStockServicr;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 成品入库接口
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-13
 */

@Slf4j
@Controller
@Api("成品入库接口")
@RequestMapping("/instock")
public class InStockController {

    @Resource
    private InStockServicr inStockService;

    /**
     * 判 转数、存入中间表
     *
     */
    @ResponseBody
    @PostMapping("/ifsuccess")
    public Result ifSuccess(@RequestBody JSONObject params){

        JSONObject nodes = new JSONObject(params.getStr("params"));
        System.out.println("测试："+nodes.getStr("uid")+nodes.getStr("stock"));
        MaterialTransationsDto materialTransationsDto = inStockService.ifSuccess(nodes.getStr("uid"), nodes.getStr("stock"));
        System.out.println("3"+materialTransationsDto.getMsg().toString());
        return Result.success(materialTransationsDto);
    }

    /**
     * 插入收货数据/设置过账状态
     * */
    @ResponseBody
    @PostMapping("/inAndUpdate")
    public Result inAndUpdateStatus(@RequestBody JSONObject params2) {

        String returnMessage = "";
        System.out.println("s:" + params2);
        JSONObject nodes = new JSONObject(params2.getStr("params2"));
        System.out.println("node:" + nodes);

        String uid = nodes.getStr("uid");
        String fromstock = nodes.getStr("fromstock");
        String tostock = nodes.getStr("tostock");
        String username = nodes.getStr("username");

        returnMessage = inStockService.inAndUpdateStatus(uid, fromstock, tostock, username);
        if (returnMessage.equals("收货成功") || returnMessage.equals("收货成功，过账315成功")) {
            return Result.success(returnMessage);
        } else {
            return Result.error("600", returnMessage);
        }
    }


    /**
     * 判 总数、设置状态为收货(暂时不用)
     *
     */
    @ResponseBody
    @PostMapping("/ins")
    public Result instockIns(@RequestBody JSONObject params){

        System.out.println("s:" + params);
        JSONObject nodes = new JSONObject(params.getStr("params"));
        System.out.println("node:" + nodes);

        MaterialTransationsDto materialTransationsDto = new MaterialTransationsDto();
        String returnMessage = "";
        if(materialTransationsDto == null){
            returnMessage = "传入数据为空";
            return Result.success(returnMessage);
        }else {
            materialTransationsDto.setQuantity(nodes.getFloat("quantity"));
            materialTransationsDto.setTransactiontype(nodes.getStr("transactiontype"));
            materialTransationsDto.setPlant(nodes.getStr("plant"));
            materialTransationsDto.setPn(nodes.getStr("pn"));
            materialTransationsDto.setBatch(nodes.getStr("batch"));
            System.out.println(materialTransationsDto);
            // 从前端获取封装的数据查找收货总数
            returnMessage = inStockService.InStock(materialTransationsDto);
        }

        return Result.success(returnMessage);
    }


}
