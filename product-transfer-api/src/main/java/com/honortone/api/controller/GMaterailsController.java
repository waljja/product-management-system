package com.honortone.api.controller;

import cn.hutool.json.JSONObject;
import com.honortone.api.common.Result;
import com.honortone.api.entity.*;
import com.honortone.api.mapper.GMterialsMapper;
import com.honortone.api.service.GMaterialsService;
import com.honortone.api.utils.SAPUtil;
import com.ktg.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 拣料接口
 * </p>
 *
 * @author 江庭明
 * @since 2023-03-20
 */

@Slf4j
@Controller
@Api("拣料接口")
@RequestMapping(value = "/sorting")
public class GMaterailsController {

    @Resource
    private GMaterialsService gMaterialsService;

    @Resource
    private GMterialsMapper gMterialsMapper;

    /**
     * 下载车牌号
     * */
    @ResponseBody
    @PostMapping(value = "/downloadcarno")
    public Result downloadCarno() {

        List<String> list = gMaterialsService.downloadCarno();
        System.out.println(list.toString());
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到车牌号");
        }
    }

    /**
     * 根据车牌号下载走货单号（有车）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadshipmentno")
    public Result downloadShipmentno(@RequestBody String carno) {

        List<String> list = gMaterialsService.downloadShipmentno(carno);
        System.out.println(list.toString());
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到相关走货单");
        }
    }

    /**
     * 根据走货编号下载to单（有车）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadtos")
    public Result downloadTos(@RequestBody String shipmentno) {

        List<String> list = gMaterialsService.downloadTos(shipmentno);
        System.out.println(list.toString());
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到相关TO单");
        }
    }

    /**
     * 根据to单查询具体备货单（有车）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadtono")
    public Result downloadTono(@RequestBody String tono) {

        List<ToList> list = gMaterialsService.downloadTono(tono);
        List<ToList> list1 = new ArrayList<>();
        ToList toList = new ToList();
        System.out.println(list.toString());
        if (list.size() > 0) {
            for (int i = 0;i < list.size();i++) {
                toList = list.get(i);
                toList.setStock(toList.getStock().toString().substring(10));
                list1.add(toList);
            }
            return Result.success(list);
        } else {
            return Result.error("600","未查询到相关TO单信息");
        }
    }

    /**
     * 成品下架
     * */
    @ResponseBody
    @PostMapping(value = "/soldout")
    public Result soldOut(@RequestBody JSONObject params) {

        JSONObject nodes = new JSONObject(params.getStr("params"));
        String cpno = nodes.getStr("cpno");
        System.out.println(cpno);
        String role = nodes.getStr("role");

        // 扫描是成品UID还是贴纸
        if (cpno.substring(0, 3).indexOf("FG2") == -1) {
            String khpn = "";
            long qty = 0;
            String rectime = "";
            String clientBatch = "";
            if (cpno.contains("/")) {
                // 查询需要有空格，不能去掉空格
                // tz = tz.replace(" ", "");
                String[] tz1 = cpno.split("/");
                khpn = tz1[0];
                qty = Long.parseLong(tz1[1]);
                rectime = tz1[2] + "-" + tz1[3] + "-" + tz1[4];
                clientBatch = tz1[5];
            }
            long sum = 0;
            // 扫描贴纸 -- 是否存在 并 找对应成品UID
            List<TagsInventory> tagsInventories = gMaterialsService.selectClientTag(khpn);
            if (tagsInventories.size() > 0) {

                long sumQuantity = gMaterialsService.getSumQuantity(tagsInventories.get(0).getUid().toString());
                ToList toList = gMaterialsService.checkTolistUID(tagsInventories.get(0).getUid().toString());
                if (toList == null)
                    return Result.error("600", "该贴纸对应成品未产生备货单，或对应成品已出库其它对应贴纸成品");

                Inventory inventory = gMterialsMapper.getInventoryInfo(toList.getUid().toString());
                sum = sumQuantity + qty;
                if (toList.getQuantity() == sum) {
                    int n1 = gMaterialsService.updateTagsStauts(clientBatch, qty);
                    if (n1 <= 0)
                        return Result.error("600", "贴纸出库失败");

                    // 全出库/出一部分
                    if (inventory.getUid_no() == sum) {
                        // 将扫描的下架数据存到成品下架表
                        int n2 = gMterialsMapper.insertInventoryOut(inventory);
                        if (n2 <= 0) {
                            return Result.error("600", "贴纸出库成功，成品下架失败1(下架表写入失败)");
                        }
                        // 库存表删除下架的成品信息
                        int n3 = gMterialsMapper.deleteiinventoryByUid(toList.getUid().toString());
                        if (n3 <= 0) {
                            return Result.error("600", "贴纸出库成功，成品下架失败2(库存表删除失败)");
                        }
                    } else {
                        inventory.setUid_no(sum);
                        int n6 = gMterialsMapper.insertInventoryOut(inventory);
                        if (n6 <= 0) {
                            return Result.error("600", "贴纸出库成功，成品部分下架失败1(下架表写入失败)");
                        }
                        int n7 = gMterialsMapper.updateQuantityStauts(inventory.getUid().toString(), (long) (inventory.getUid_no() - sum), 1);
                        if (n7 <= 0)
                            return Result.error("600", "贴纸出库成功，库存数量修改失败");

                    }

                    int n4 = gMterialsMapper.updateTono(toList.getUid().toString());
                    if (n4 <= 0)
                        return Result.error("600", "贴纸出库后对应成品UID出库失败");

                    // 查询即存在已拣货和未拣货备货单（TO明细表）
                    int n5 = gMterialsMapper.checkStauts();
                    if (n5 > 0) {
                        // 更新TO管理表对应备货单为拣货中
                        gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
                    } else {
                        gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
                    }

                    return Result.success("成品出库成功");

                } else if (toList.getQuantity() > sum) {
                    int n = gMaterialsService.updateTagsStauts(clientBatch, qty);
                    if (n <= 0)
                        return Result.error("600", "贴纸出库失败！");

                    // 查询即存在已拣货和未拣货备货单（TO明细表）
                    int n1 = gMterialsMapper.checkStauts();
                    if (n1 > 0) {
                        // 更新TO管理表对应备货单为拣货中
                        gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
                    } else {
                        gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
                    }
                    return Result.success("贴纸出库成功！");

                } else if (toList.getQuantity() < sum) {
                    return Result.error("600", "出库数量大于应出库数量，应出库" + (sum - (sum - toList.getQuantity())));
                }
            }

        } else {
            int n = gMterialsMapper.checkTags(cpno);
            if (n > 0) {
                Float quantityTolist = gMterialsMapper.checkQuantityByUid(cpno) == null ? 0 : gMterialsMapper.checkQuantityByUid(cpno);
                long quantityTags = gMterialsMapper.getSumQuantity(cpno);
                if (quantityTags != quantityTolist) {
                    return Result.error("600", "该成品单贴纸未拣货完成");
                } else {
                    return Result.error("600", "该成品已出库");
                }
            } else {
                String YorN = gMaterialsService.checkQuantity(cpno);
                if ("NA".equals(YorN))
                    return Result.error("600", "未找到该成品");

                if (!"Y".equals(YorN))
                    return Result.error("600", "该UID未拆分，请拆分UID数量与备货数量相等");

                String returnMessage = gMaterialsService.soldOut(cpno, role);
                System.out.println(returnMessage);
                // 若前端返回没有提示信息，则是成品已存到下架表，还未在库存表中删除/删除失败
                if ("成品出库成功".equals(returnMessage) || "替换拣料成功！".equals(returnMessage)) {
                    return Result.success(returnMessage);
                } else {
                    return Result.error("600", returnMessage);
                }
            }
        }
         return Result.error("600", "未知错误！！！");
    }

    /**
     * 统计剩余数量
     * */
    @ResponseBody
    @PostMapping(value = "/getqty")
    public Result getQty(@RequestBody String tono) {

        List<Map<Integer, Integer>> list = gMaterialsService.getQty(tono);
        System.out.println(list.toString());
        // 若前端返回没有提示信息，则是成品已存到下架表，还未在库存表中删除/删除失败
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","查询未果");
        }
    }

    /**
     * 下载走货单（无车情况）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadshipmentno2")
    public Result downloadShipmentNo2() {

        List<String> list = gMaterialsService.downloadShipmentNo2();
        System.out.println(list.toString());
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到相关走货单信息");
        }
    }

    /**
     * 下载备货单（无车情况）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadorder")
    public Result downloadOrder(@RequestBody String shipmentno) {

        List<String> list = gMaterialsService.downloadTos2(shipmentno);
        System.out.println(list.toString());
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到相关TO单信息，请检查该走货单是否有车");
        }
    }

    /**
     * 下载手动上传的走货资料 进行手动备货
     * */
    @ResponseBody
    @PostMapping(value = "/downloadshipment")
    public Result downloadShipment(@RequestBody JSONObject item) {

        System.out.println(item.toString());

        // 第一次下载/没有筛选的单点确认后查询
        List<ShipmentInfoByhand> list = gMaterialsService.downloadShipmentinfoByhand();
        if (list.size() > 0) {
            System.out.println("测试" + list.get(0));
            return Result.success(list);
        } else {
            return Result.error("600", "未查询到相关备货单！");
        }
//        if (item.toString().equals("{}")) {
//            // 第一次下载/没有筛选的单点确认后查询
//            List<ShipmentInfoByhand> list = gMaterialsService.downloadShipmentinfoByhand();
//            if (list.size() > 0) {
//                System.out.println("测试" + list.get(0));
//                return Result.success(list);
//            } else {
//                return Result.error("600", "未查询到相关备货单！");
//            }
//        } else {
//            // 筛选后 单点确认后查询
//            System.out.println(item.getStr("client").toString());
//            List<ShipmentInfoByhand> list = gMterialsMapper.downloadShipmentinfoByhand2(item.getStr("client").toString());
//            if (list.size() > 0) {
//                System.out.println("测试" + list.get(0));
//                return Result.success(list);
//            } else {
//                return Result.error("600", "未查询到相关备货单！");
//            }
//        }
    }

    /**
     * 根据客户获取手动走货信息
     * */
    @ResponseBody
    @PostMapping(value = "/getshipmentinfo")
    public Result getShipmentInfo(@RequestBody String cilent) {

        List<ShipmentInfoByhand> list = gMaterialsService.getShipmentInfo(cilent);
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600", "未查询到相关客户走货信息");
        }
    }

    /**
     * 手动备货完后 单个确认操作
     * */
    @ResponseBody
    @PostMapping(value = "/onconfirm")
    public Result onConfirm(@RequestBody JSONObject item) {

        System.out.println(item.getInt("id"));

        if (gMaterialsService.updateByid(item.getInt("id")) > 0) {
            return Result.success("已确认手动备货");
        } else {
            return Result.error("600", "手动备货确认失败！");
        }
    }

    /**
     * 手动备货完后 一键确认操作
     * */
    @ResponseBody
    @PostMapping(value = "/allconfirm")
    public Result allConfirm(@RequestBody String bill) {

        if (gMaterialsService.updateAllByid(bill) > 0) {
            return Result.success("已确认手动备货");
        } else {
            return Result.error("600", "手动备货确认失败！");
        }
    }

    /**
     * 手动备货完后 一键确认操作
     * */
    @ResponseBody
    @PostMapping(value = "/getquantity")
    public Result getQuantity(@RequestBody String bill_1) {

        List<Map<Integer, Integer>> list = gMaterialsService.getQuantity(bill_1);
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","查询未果");
        }
    }

    /** 更新备货清单
     *
     * */
    @ResponseBody
    @PostMapping(value = "/updatetono")
    public Result updateToNo(String toNo) {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDate = simpleDateFormat.format(date);

        SAPUtil sapUtil = new SAPUtil();
        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS_1(startDate, startDate);
        // 实体类对象存到List集合中，统计对象中某个值相同的数据

        for (int i = 0;i < list.size();i++) {
            if (list.get(i).getLastComfirm() != null && (list.get(i).getLastComfirm().equals("船务") || list.get(i).getLastComfirm().equals("货仓"))) {
                System.out.println(list.get(i).toString());
            }
        }

        return null;
    }

}
