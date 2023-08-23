package com.honortone.api.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.honortone.api.common.Result;
import com.honortone.api.service.BindStockService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 成品绑库接口
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-17
 */
@Slf4j
@Controller
@Api("成品绑库接口")
@RequestMapping("/bindstock")
public class BindStockController {

    @Resource
    private BindStockService bindStockService;

    /**
     * 不需要扫描外贴纸
     *
     * **/
    @ResponseBody
    @PostMapping("/bindstock_1")
    public Result BindStock_1(@RequestBody JSONObject params) {

//        // s:{"params":{"cp":"uid","kw":"stock"}}
//        //{"params":{"cp":"FG202383092640651101","kw":"B2222"}}
//        // 前端获取的json字符串转换成json格式
//        JSONObject json = new JSONObject(params);
//        System.out.println("json:" + json);
//        // json:{"params":{"cp":"uid","kw":"stock"}}
//        // 获取其中的键值对（params名与前端参数名一致【非具体参数名】）
//        JSONObject nodes = new JSONObject(json.getStr("params"));
//        System.out.println("node:" + nodes);
//        // json:node{"cp":"45","kw":"54"}
//        System.out.println("uid:" + nodes.getStr("cp"));
//        // uid:---


        System.out.println("s:" + params);
        JSONObject nodes = new JSONObject(params.getStr("params"));
        String uid = nodes.getStr("cp");
        String stock = nodes.getStr("kw");
        String username = nodes.getStr("username");

        // 不允许重复绑库上架（未拣货之前都可以重复扫描上架，只改变库位【移库功能】），拣货了不能再上架，只能回仓
        int n = bindStockService.checkUID(uid);
        if (n > 0)
            return Result.error("600", "该成品已拣货出库，不允许再上架，请检查是否需要做回仓");

        String returnMessage = bindStockService.bindStock_1(uid, stock, username);

        return Result.success(returnMessage);
    }

    /**
     * 扫描外贴纸前插入模板数据（方便与贴纸关联）
     *
     * **/
    @ResponseBody
    @PostMapping("/firstbind")
    public Result FirstBind(String uid) {

        System.out.println("第一次写入" + uid);
        String returnMessage = bindStockService.firstInsert(uid);
        if (returnMessage.equals("扫描贴纸前插入失败") || returnMessage.equals("转数失败，不能绑库")) {
            return Result.error();
        }
        return Result.success(returnMessage);
    }

    public String FirstBind_1(String uid) {

        System.out.println("第一次写入" + uid);
        String returnMessage = bindStockService.firstInsert(uid);
        return returnMessage;
    }

    /**
     * 需要扫描贴纸
     *
     * **/
    @ResponseBody
    @PostMapping("/bindstock_2")
    public Result BindStock_2(@RequestBody JSONObject params) {

        System.out.println("s:" + params);
        JSONObject nodes = new JSONObject(params.getStr("params"));
        String uid = nodes.getStr("cp");
        String stock = nodes.getStr("kw");
        String tz = nodes.getStr("tz");

        String htpn = "";
        String khpn = "";
        String rectime = "";
        long qty = 0;
        String clientBatch = "";
        if (tz.contains("@")) {
            htpn = tz.split("@P")[1];
            htpn = htpn.split("@T")[0];
        } else if (tz.contains("/")){
            // 查询需要有空格，不能去掉空格
            // tz = tz.replace(" ", "");
            String[] tz1 = tz.split("/");
            khpn = tz1[0];
            qty = Long.parseLong(tz1[1]);
            rectime = tz1[2] + "-" + tz1[3] + "-" + tz1[4];
            clientBatch = tz1[5];
        }
        // String s = FirstBind_1(uid);
        System.out.println("测试" + tz);
        String returnMessage = bindStockService.bindStock_2(uid, tz, stock, htpn, khpn, rectime, qty, clientBatch);
        if (returnMessage.equals("HT贴纸绑库成功") || returnMessage.equals("绑定客户贴纸成功")) {
            return Result.success(returnMessage);
        } else {
            return Result.error("600",returnMessage);
        }

    }

    @ResponseBody
    @PostMapping("/rollback")
    public Result RollBack(@RequestBody JSONObject params) {

        System.out.println("s:" + params);
        JSONObject nodes = new JSONObject(params.getStr("params"));
        String uid = nodes.getStr("uid");
        System.out.println(uid);
        String rollbackReason = nodes.getStr("rollbackReason");

        if ("".equals(rollbackReason) || rollbackReason == null)
            return Result.error("600", "请选择回仓原因");

        String returnMessage = bindStockService.RollBack(uid, rollbackReason);
        if (returnMessage.equals("回仓成功")) {
            return Result.success(returnMessage);
        } else {
            return Result.error("600", returnMessage);
        }

    }

    @ResponseBody
    @PostMapping("/bindarea")
    public Result BindArea(@RequestBody JSONObject params) {

        JSONObject jsonObject = new JSONObject(params.getStr("params"));
        String uid = jsonObject.getStr("uid");
        String stock = jsonObject.getStr("stock");

        String returnMessage = bindStockService.checkBHandQH(uid);
        if (!returnMessage.contains("BH"))
            return Result.error("600", returnMessage);

        int n = bindStockService.updateTosStock(returnMessage, stock);
        if (n > 0) {
            int qh_sum = bindStockService.checkStatusTosQH(returnMessage);
            if (qh_sum > 0) {
                return Result.success("绑定走货区成功，对应走货单存在欠货，欠货数量：" + qh_sum);
            } else {
                return Result.success("绑定走货区成功");
            }
        } else if (n == -10) {
            return Result.error("600","走货资料有变动，请更新");
        } else {
            return Result.error("600","绑定走货区失败");
        }
    }

}
