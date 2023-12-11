package com.honortone.api.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.honortone.api.common.Result;
import com.honortone.api.entity.*;
import com.honortone.api.mapper.GMterialsMapper;
import com.honortone.api.service.GMaterialsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.IOException;
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

    private static volatile int Guid = 100;


    @Resource
    private GMaterialsService gMaterialsService;

    @Resource
    private GMterialsMapper gMterialsMapper;

    /**
     * 根据日期下载车牌号
     * */
    @ResponseBody
    @PostMapping(value = "/downloadcarno")
    public Result downloadCarno(@RequestBody String date) {

        List<String> list = gMaterialsService.downloadCarno(date);
        System.out.println(list.toString());
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到车牌号");
        }
    }

    /**
     * 根据车牌号、日期下载走货单号（有车）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadshipmentno")
    public Result downloadShipmentno(@RequestBody JSONObject params) {

        JSONObject nodes = new JSONObject(params.getStr("params"));
        String carno = nodes.getStr("carno");
        String date = nodes.getStr("date");
        System.out.println(carno + date);

        List<String> list = gMaterialsService.downloadShipmentno(carno, date);
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
                if (toList.getStock() != null && toList.getStock() != "" && toList.getStock().length() > 10) {
                    toList.setStock(toList.getStock().toString().substring(10));
                }
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
//    @ResponseBody
//    @PostMapping(value = "/soldout")
//    public Result soldOut(@RequestBody JSONObject params) {
//
//        JSONObject nodes = new JSONObject(params.getStr("params"));
//        String cpno = nodes.getStr("cpno");
//        System.out.println(cpno);
//        String role = nodes.getStr("role");
//        String toNo = nodes.getStr("bill");
//        if (cpno.length() < 2)
//            return Result.error("600", "请正确扫描成品单");
//
//        // 扫描是成品UID还是贴纸
//        if (cpno.substring(0, 2).indexOf("FG") == -1) {
//            String khpn = "";
//            long qty = 0;
//            String rectime = "";
//            String clientBatch = "";
//            if (cpno.contains("/")) {
//                // 查询需要有空格，不能去掉空格
//                // tz = tz.replace(" ", "");
//                String[] tz1 = cpno.split("/");
//                khpn = tz1[0];
//                qty = Long.parseLong(tz1[1]);
//                rectime = tz1[2] + "-" + tz1[3] + "-" + tz1[4];
//                clientBatch = tz1[5];
//
//                float sum = 0;
//                // 扫描贴纸 -- 是否存在 并 找对应成品UID
//                List<TagsInventory> tagsInventories = gMaterialsService.selectClientTag(khpn, clientBatch);
//                System.out.println("11");
//                if (tagsInventories.size() > 0) {
//
//                    System.out.println(tagsInventories.get(0).getUid().toString());
//                    // 判UID是否有预留
//                    ToList toList = gMaterialsService.checkTolistUID(tagsInventories.get(0).getUid().toString());
//                    if (toList == null)
//                        return Result.error("600", "该贴纸对应成品未产生备货单，或对应成品已出库其它对应贴纸成品");
//
//                    Inventory inventory = gMterialsMapper.getInventoryInfo(toList.getUid().toString());
//                    float quantity_tolist = toList.getQuantity();
//                    if (Float.compare(inventory.getUid_no(), quantity_tolist) > 0) {
//                        System.out.println(inventory.getUid_no() + "===" + quantity_tolist);
//                        return Result.error("600", "该贴纸对应UID实际绑定贴纸数量为" + inventory.getUid_no() + "，应该出数量为" + quantity_tolist + ",请拆分UID数量与备货数量相等（扫贴纸提示）");
//                    }
//                    System.out.println("22");
//                    // 存在异议
//                    long sumQuantity = gMterialsMapper.getSumQuantity2(tagsInventories.get(0).getUid().toString());
//                    sum = sumQuantity + qty;
//                    int result = Float.compare(toList.getQuantity(), sum);
//                    if (result == 0) {
//                        System.out.println(clientBatch);
//                        int n1 = gMaterialsService.updateTagsStauts(khpn, clientBatch, qty);
//                        if (n1 <= 0)
//                            return Result.error("600", "贴纸出库失败");
//
//                        // 全出库/出一部分
//                        if (inventory.getUid_no() == sum) {
//                            // 判断该贴纸UID是否还存在未出库批次
////                            List<TagsInventory> tagsInventoryList = gMterialsMapper.checkTagsByUID(inventory.getUid().toString());
////                            if (tagsInventoryList.size() > 0) {
////                                // 查询拆分后的UID
////                                String SplitUID = gMterialsMapper.getSplitUID(inventory.getUid().toString()) == null ? "" : gMterialsMapper.getSplitUID(inventory.getUid().toString());
////                                if (!"".equals(SplitUID)) {
////                                    // 将剩余的贴纸信息插入到拆分后的UID中
////                                    System.out.println("待写完。。。");
////
////                                }
////                            }
//
//                            // 将扫描的下架数据存到成品下架表
//                            int n2 = gMterialsMapper.insertInventoryOut(inventory);
//                            if (n2 <= 0) {
//                                return Result.error("600", "贴纸出库成功，成品下架失败1(下架表写入失败)");
//                            }
//                            // 库存表删除下架的成品信息
//                            int n3 = gMterialsMapper.deleteiinventoryByUid(toList.getUid().toString());
//                            if (n3 <= 0) {
//                                return Result.error("600", "贴纸出库成功，成品下架失败2(库存表删除失败)");
//                            }
//                        } else {
//                            System.out.println("33");
//                            inventory.setUid_no(sum);
//                            int n6 = gMterialsMapper.insertInventoryOut(inventory);
//                            if (n6 <= 0) {
//                                return Result.error("600", "贴纸出库成功，成品部分下架失败1(下架表写入失败)");
//                            }
//                            int n7 = gMterialsMapper.updateQuantityStauts(inventory.getUid().toString(), (long) (inventory.getUid_no() - sum), 1);
//                            if (n7 <= 0)
//                                return Result.error("600", "贴纸出库成功，库存数量修改失败");
//
//                        }
//
//                        int n4 = gMterialsMapper.updateTono(toList.getUid().toString());
//                        if (n4 <= 0)
//                            return Result.error("600", "贴纸出库后对应成品UID出库失败");
//
//                        // 查询即存在已拣货和未拣货备货单（TO明细表）
//                        int n5 = gMterialsMapper.checkStauts(toList.getUid().toString());
//                        if (n5 > 0) {
//                            // 更新TO管理表对应备货单为拣货中
//                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
//                        } else {
//                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
//                        }
//
//                        return Result.success("成品出库成功2");
//
//                    } else if (result > 0) {
//                        System.out.println(clientBatch);
//                        int n = gMaterialsService.updateTagsStauts(khpn, clientBatch, qty);
//                        if (n <= 0)
//                            return Result.error("600", "贴纸出库失败！");
//
//                        // 查询即存在已拣货和未拣货备货单（TO明细表）
//                        int n1 = gMterialsMapper.checkStauts(toList.getUid().toString());
////                        if (n1 > 0) {
////                            // 更新TO管理表对应备货单为拣货中
////                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
////                        } else {
////                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
////                        }
//                        return Result.success("贴纸出库成功！");
//
//                    } else if (result < 0) {
//                        System.out.println(toList.getQuantity() + "==" + sum);
//                        return Result.error("600", "出库数量大于应出库数量，应出库" + (sum - (sum - toList.getQuantity())));
//                    }
//                } else {
//                    return Result.error("600", "不存在相关贴纸，或该贴纸已出库！");
//                }
//            } else if (cpno.contains("@")) {
//                return Result.error("600" ,"鸿通贴纸");
//            } else {
//
//            }
//
//        } else {
//            // 是否绑定贴纸
//            int n = gMterialsMapper.checkTags(cpno);
//            if (n > 0) {
//                // 预留数量
//                Float quantityTolist = gMterialsMapper.checkQuantityByUid(cpno) == null ? 0 : gMterialsMapper.checkQuantityByUid(cpno);
//                // 贴纸数量
//                float quantityTags = gMterialsMapper.getSumQuantity(cpno);
//                int result = Float.compare(quantityTags, quantityTolist);
//                if (result > 0) {
//                    System.out.println(quantityTags + "==2==" + quantityTolist);
//                    return Result.error("600", "该UID剩余绑定贴纸总数大于预留数量，不能扫UID下架所有贴纸，请扫描贴纸下架");
//                } else if (result < 0){
//                    System.out.println(quantityTags + "==1==" + quantityTolist);
//                    return Result.error("600", "该UID剩余绑定贴纸总数小于预留数量");
//                } else if (result == 0) {
//                    System.out.println("可以一键下架");
//                    // 下架所有绑定的贴
//                    int allStatus = gMterialsMapper.updateTagsStautsAll(cpno);
//                    if (allStatus > 0) {
//                        Inventory inventory = gMterialsMapper.getInventoryInfo(cpno);
//                        gMterialsMapper.insertInventoryOut(inventory);
//                        // 设置为已拣货
//                        // 库存表删除下架的成品信息
//                        gMterialsMapper.deleteiinventoryByUid(cpno);
//                        // 更新TO明细表对应备货单为已拣货
//                        gMterialsMapper.updateTono(cpno);
//
//                    } else {
//                        return Result.error("600","更新贴纸失败");
//                    }
//                    // 更新拣货状态
//                    int n1 = gMterialsMapper.checkStauts(cpno);
//                    if (n1 > 0) {
//                        // 更新TO管理表对应备货单为拣货中
//                        gMterialsMapper.updateTosBHStatus(cpno, 1);
//                    } else {
//                        gMterialsMapper.updateTosBHStatus(cpno, 2);
//                    }
//                    return Result.success("所有贴纸出库成功");
//                }
//            } else {
//                String YorN = gMaterialsService.checkQuantity(cpno);
//                if ("UID不存在库存".equals(YorN))
//                    return Result.error("600", "未找到该成品或该成品已拣货");
//
//                if ("库存与备货数量不等".equals(YorN))
//                    return Result.error("600", "该UID未拆分，请拆分UID数量与备货数量相等");
//
//                String returnMessage = gMaterialsService.soldOut(cpno, role, toNo);
//                System.out.println(returnMessage);
//                // 若前端返回没有提示信息，则是成品已存到下架表，还未在库存表中删除/删除失败
//                if ("成品出库成功".equals(returnMessage) || "替换拣料成功！".equals(returnMessage)) {
//                    return Result.success(returnMessage);
//                } else {
//                    return Result.error("600", returnMessage);
//                }
//            }
//        }
//         return Result.error("600", "未知错误！！！");
//    }

    @ResponseBody
    @PostMapping(value = "/soldout")
    public Result soldOut(@RequestBody JSONObject params) {

        JSONObject nodes = new JSONObject(params.getStr("params"));
        String cpno = nodes.getStr("cpno");
        System.out.println(cpno);
        String role = nodes.getStr("role");
        String toNo = nodes.getStr("bill");
        if (cpno.length() < 2)
            return Result.error("600", "请正确扫描成品单");

        // 扫描是成品UID还是贴纸
        if (cpno.substring(0, 2).indexOf("FG") == -1) {
            String khpn = "";
            long qty = 0;
            String rectime = "";
            String clientBatch = "";
            // 珠飞贴纸
            if (cpno.contains("/")) {
                // 查询需要有空格，不能去掉空格
                // tz = tz.replace(" ", "");
                String[] tz1 = cpno.split("/");
                khpn = tz1[0];
                qty = Long.parseLong(tz1[1]);
                rectime = tz1[2] + "-" + tz1[3] + "-" + tz1[4];
                clientBatch = tz1[5];

                float sum = 0;
                // 扫描贴纸 -- 是否存在 并 找对应成品UID
                List<TagsInventory> tagsInventories = gMaterialsService.selectClientTag(khpn, clientBatch);
                List<TagsInventory> tagsInventories2 = gMaterialsService.selectClientTag2(khpn, clientBatch);
                System.out.println("11");
                if (tagsInventories.size() > 0) {

                    System.out.println(tagsInventories.get(0).getUid().toString());
                    // 判UID是否有预留
                    ToList toList = gMaterialsService.checkTolistUID(tagsInventories.get(0).getUid().toString());
                    if (toList == null) {

                        System.out.println("111");
                        // 根据备货单找所有UID对应的绑定的贴纸，如果存在与扫描的贴纸条件符合的则直接替换（可设置管理员权限），不符合则不能拣
                        // 查询对应TO单是否存在同PN，贴纸同数量的数据
                        List<TagsInventory> tagsInventoryList = gMterialsMapper.getTolistAndTagsInfo(toNo, tagsInventories.get(0).getPn().toString(), tagsInventories.get(0).getQuantity());
                        if (tagsInventoryList.size() > 0) {
                            System.out.println("222");
                            // boolean flagPO = tagsInventoryList.stream().filter(TagsInventory->TagsInventory.getPo().equals(tagsInventories.get(0).getPo())).findAny().isPresent();
                            TagsInventory tagsInventory = new TagsInventory();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            boolean flag = false;
                            for (int i = 0;i < tagsInventoryList.size();i++) {
                                tagsInventory = tagsInventoryList.get(i);
                                if (tagsInventoryList.get(i).getPo().equals(tagsInventories.get(0).getPo()) && sdf.format(tagsInventoryList.get(i).getProductionDate()).equals(sdf.format(tagsInventories.get(0).getProductionDate()))) {
                                    flag = true;
                                    break;
                                }
                            }
                            // 必须先判是否为TURE，不能直接判manager
                            if (flag == true) {
                                System.out.println("允许替换操作");
                            } else if (!role.equals("manager")) {
                                return Result.error("600", "替换贴纸日期或PO不相等，预留PO：" + tagsInventory.getPo() + " 日期：" + sdf.format(tagsInventory.getProductionDate()) + ",请切换管理员账号操作");
                            }
                            // 被替换UID已拣贴纸数量
                            long sumQuantity = gMterialsMapper.getSumQuantity2(tagsInventory.getUid().toString());
                            ToList toList1 = gMaterialsService.checkTolistUID(tagsInventory.getUid().toString());
                            if (sumQuantity + tagsInventories.get(0).getQuantity() > toList1.getQuantity())
                                return Result.error("600", "替换后数量大于预留数量，不能替换");

                            // 更新替换和被替换的UID对应的贴纸信息
                            tagsInventories.get(0).setStatus(1l);
                            // 更新 -- 被替换
                            int n = gMterialsMapper.updateTagsInfo(tagsInventories.get(0), tagsInventory);
                            if (n > 0) {
                                System.out.println("333");
                                // 更新替换
                                int n1 = gMterialsMapper.updateTagsInfo(tagsInventory, tagsInventories.get(0));
                                if (n1 < 0)
                                    return Result.error("600", "替换信息修改失败2");

                            } else {
                                return Result.error("600", "被替换信息修改失败1");
                            }
                            if ((sumQuantity + tagsInventories.get(0).getQuantity()) == toList1.getQuantity()) {
                                Inventory inventory = gMterialsMapper.getInventoryInfo(toList1.getUid().toString());
                                // 判断预留数量是否等于库存数量 （全出库/出一部分）
                                if (inventory.getUid_no() == toList1.getQuantity()) {
                                    System.out.println("444");
                                    // 预留 == 库存
                                    // 将扫描的下架数据存到成品下架表
                                    int n2 = gMterialsMapper.insertInventoryOut(inventory);
                                    if (n2 <= 0) {
                                        return Result.error("600", "贴纸出库成功，成品下架失败1(下架表写入失败)");
                                    }
                                    // 库存表删除下架的成品信息
                                    int n3 = gMterialsMapper.deleteiinventoryByUid(toList1.getUid().toString());
                                    if (n3 <= 0) {
                                        return Result.error("600", "贴纸出库成功，成品下架失败2(库存表删除失败)");
                                    }
                                } else {
                                    System.out.println("555");
                                    // 预留的数量小于库存数量
                                    System.out.println("33");
                                    float uidNo = inventory.getUid_no();
                                    // 该数量为预留的数量（即下架的数量 sumQuantity + tagsInventories.get(0).getQuantity() ）
                                    inventory.setUid_no(toList1.getQuantity());
                                    inventory.setTagsQuantity(toList1.getQuantity());
                                    int n6 = gMterialsMapper.insertInventoryOut(inventory);
                                    if (n6 <= 0) {
                                        return Result.error("600", "贴纸出库成功，成品部分下架失败1(下架表写入失败)");
                                    }
                                    // 库存表删除下架的成品信息
                                    int n3 = gMterialsMapper.deleteiinventoryByUid(inventory.getUid().toString());
                                    if (n3 <= 0) {
                                        return Result.error("600", "贴纸出库成功，成品下架失败2(库存表删除失败)");
                                    }

                                    // 新产生一个UID存放剩余的绑定的贴纸信息
                                    Inventory inventory1 = new Inventory();
                                    String uid = getGuid();
                                    BeanUtil.copyProperties(inventory, inventory1);
                                    System.out.println("库存赋值(替换)：" + inventory1);
                                    inventory1.setUid(uid);
                                    inventory1.setUid_no(uidNo - toList1.getQuantity());
                                    inventory1.setStatus(1);

                                    // 将剩余的贴纸信息放到新产生的库存UID中
                                    List<TagsInventory> tagsInventoryList1 = gMterialsMapper.checkTagsByUID(inventory.getUid().toString());
                                    System.out.println("测试:" + tagsInventoryList1.toString());
                                    for (TagsInventory tagsInventory1 : tagsInventoryList1) {
                                        tagsInventory1.setUid(inventory1.getUid().toString());
                                    }
                                    long sum_tags = tagsInventoryList1.stream().mapToLong(TagsInventory::getQuantity).sum();
                                    System.err.println(sum_tags);
//                                    long tags_qty = (long) (uidNo - sum_tags);
                                    inventory1.setTagsQuantity(sum_tags);
                                    int n7 = gMterialsMapper.insertInventory(inventory1);
                                    if (n7 <= 0)
                                        return Result.error("600", "贴纸出库成功，新增UID失败");

                                    System.out.println(tagsInventoryList1.toString());
                                    int n8 = gMterialsMapper.updatetagsInfo1(tagsInventoryList1);
                                    if (n8 <= 0)
                                        return Result.error("600","剩余贴纸转移失败");

                                }
                                int n4 = gMterialsMapper.updateTono(toList1.getUid().toString());
                                if (n4 <= 0)
                                    return Result.error("600", "贴纸出库后对应成品UID出库失败");

                                // 查询即存在已拣货和未拣货备货单（TO明细表）
                                int n5 = gMterialsMapper.checkStauts(toList1.getUid().toString());
                                if (n5 > 0) {
                                    // 更新TO管理表对应备货单为拣货中
                                    gMterialsMapper.updateTosBHStatus(toList1.getUid().toString(), 1);
                                } else {
                                    gMterialsMapper.updateTosBHStatus(toList1.getUid().toString(), 2);
                                }
                            }
                            return Result.success("贴纸替换成功,剩余出库数量：" + (toList1.getQuantity() - (sumQuantity + tagsInventories.get(0).getQuantity())));

                            // boolean flagDate = tagsInventoryList.stream().anyMatch(TagsInventory -> TagsInventory.getProductionDate().equals(tagsInventories.get(0).getProductionDate()));

                        } else {
                            System.out.println("预留的TO单不存在可替换的贴纸成品");
                            return Result.error("600", "预留的TO单不存在可替换的贴纸成品");
                        }
                    } else {
                        // UID被预留，但不是当前备货单预留，不能执行替换操作
                        if (!toList.getToNo().toString().equals(toNo))
                            return Result.error("600", "该贴纸被其它备货单(" + toList.getToNo().toString() + ")预留，不能出库");

                        // 根据预留UID查询库存信息
                        Inventory inventory = gMterialsMapper.getInventoryInfo(toList.getUid().toString());
                        // 预留数量
                        float quantity_tolist = toList.getQuantity();
                        // 库存数量大于预留数量
//                    if (Float.compare(inventory.getUid_no(), quantity_tolist) > 0) {
//                        System.out.println(inventory.getUid_no() + "===" + quantity_tolist);
//                        return Result.error("600", "该贴纸对应UID实际绑定贴纸数量为" + inventory.getUid_no() + "，应该出数量为" + quantity_tolist + ",请拆分UID数量与备货数量相等（扫贴纸提示）");
//                    }
                        System.out.println("22");
                        // 查询已拣的贴纸数量
                        long sumQuantity = gMterialsMapper.getSumQuantity2(tagsInventories.get(0).getUid().toString());
                        sum = (float) sumQuantity + (float) qty;
                        // 比较预留数量和已拣贴纸数量
                        int result = Float.compare(toList.getQuantity(), sum);
                        if (result == 0) {
                            // 预留数量等于 已拣贴纸数量+当前扫描的贴纸数量
                            System.out.println(clientBatch);
                            // 更新当前扫描贴纸状态
                            int n1 = gMaterialsService.updateTagsStauts(khpn, clientBatch, qty);
                            if (n1 <= 0)
                                return Result.error("600", "贴纸出库失败");

                            // 判断预留数量是否等于库存数量 （全出库/出一部分）
                            if (inventory.getUid_no() == sum) {
                                System.out.println("555");
                                // 判断该贴纸UID是否还存在未出库批次
//                            List<TagsInventory> tagsInventoryList = gMterialsMapper.checkTagsByUID(inventory.getUid().toString());
//                            if (tagsInventoryList.size() > 0) {
//                                // 查询拆分后的UID
//                                String SplitUID = gMterialsMapper.getSplitUID(inventory.getUid().toString()) == null ? "" : gMterialsMapper.getSplitUID(inventory.getUid().toString());
//                                if (!"".equals(SplitUID)) {
//                                    // 将剩余的贴纸信息插入到拆分后的UID中
//                                    System.out.println("待写完。。。");
//
//                                }
//                            }
                                // 预留 == 库存
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
                                System.out.println("666");
                                // 预留的数量小于库存数量
                                System.out.println("33");
                                float uidNo = inventory.getUid_no();
                                inventory.setUid_no(sum);
                                inventory.setTagsQuantity((long) sum);
                                int n6 = gMterialsMapper.insertInventoryOut(inventory);
                                if (n6 <= 0) {
                                    return Result.error("600", "贴纸出库成功，成品部分下架失败1(下架表写入失败)");
                                }
                                int n3 = gMterialsMapper.deleteiinventoryByUid(inventory.getUid().toString());
                                if (n3 <= 0) {
                                    return Result.error("600", "贴纸出库成功，成品下架失败3(库存表删除失败)");
                                }

                                // 新产生一个UID存放剩余的绑定的贴纸信息
                                Inventory inventory1 = new Inventory();
                                String uid = getGuid();
                                BeanUtil.copyProperties(inventory, inventory1);
                                System.out.println("库存赋值：" + inventory1);
                                inventory1.setUid(uid);
                                inventory1.setUid_no(uidNo - sum);
                                inventory1.setStatus(1);
                                // int n7 = gMterialsMapper.updateQuantityStauts(inventory.getUid().toString(), (long) (inventory.getUid_no() - sum), 1);
                                // 将剩余的贴纸信息放到新产生的库存UID中
                                List<TagsInventory> tagsInventoryList1 = gMterialsMapper.checkTagsByUID(inventory.getUid().toString());
                                System.out.println("1121" + tagsInventoryList1);
                                for (TagsInventory tagsInventory1 : tagsInventoryList1) {
                                    tagsInventory1.setUid(inventory1.getUid().toString());
                                }
                                long sum_tags = tagsInventoryList1.stream().mapToLong(TagsInventory::getQuantity).sum();
                                System.err.println(sum_tags);
//                                long tags_qty = (long) (uidNo - sum_tags);
                                inventory1.setTagsQuantity(sum_tags);
                                System.out.println("数量" + inventory1.toString());
                                int n7 = gMterialsMapper.insertInventory(inventory1);
                                if (n7 <= 0)
                                    return Result.error("600", "贴纸出库成功，新增UID失败");

                                System.out.println("1212" + tagsInventoryList1);
                                int n8 = gMterialsMapper.updatetagsInfo1(tagsInventoryList1);
                                if (n8 < 0)
                                    return Result.error("600","剩余贴纸转移失败");

                            }
                            // 更新预留表预留的UID装维为已拣货
                            int n4 = gMterialsMapper.updateTono(toList.getUid().toString());
                            if (n4 <= 0)
                                return Result.error("600", "贴纸出库后对应成品UID出库失败");

                            // 查询即存在已拣货和未拣货备货单（TO明细表）
                            int n5 = gMterialsMapper.checkStauts(toList.getUid().toString());
                            if (n5 > 0) {
                                // 更新TO管理表对应备货单为拣货中
                                gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
                            } else {
                                gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
                            }

                            return Result.success("成品出库成功");

                        } else if (result > 0) {
                            System.out.println("777");
                            // 预留数量比已拣数量大，则允许继续扫描贴纸出库（拣货）
                            System.out.println(clientBatch);
                            // 更新贴纸状态为已拣货
                            int n = gMaterialsService.updateTagsStauts(khpn, clientBatch, qty);
                            if (n <= 0)
                                return Result.error("600", "贴纸出库失败！");

                            // 查询即存在已拣货和未拣货备货单（TO明细表）
//                        int n1 = gMterialsMapper.checkStauts(toList.getUid().toString());
//                        if (n1 > 0) {
//                            // 更新TO管理表对应备货单为拣货中
//                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
//                        } else {
//                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
//                        }
                            return Result.success("贴纸出库成功！剩余未拣贴纸数量" + (quantity_tolist - sum));

                        } else if (result < 0) {
                            // 已拣贴纸数量加当前扫描的贴纸数量大于预留数量
                            System.out.println(toList.getQuantity() + "==" + sum);
                            return Result.error("600", "出库数量大于应出库数量，应出库" + (sum - (sum - toList.getQuantity())));
                        }
                    }
                } else if (tagsInventories2.size() > 0) {
                    return Result.error("600", "该贴纸已出库！");
                } else {
                    return Result.error("600", "该贴纸未绑定UID！");
                }
            } else if (!cpno.contains("/") && !cpno.contains("@")) {

                String s = soldOutCC4U(toNo, role, cpno);
                if (s.contains("N")) {
                    return Result.error("600" ,s);
                } else {
                    return Result.success(s);
                }
            }

        } else {
            // 是否绑定贴纸
            int n = gMterialsMapper.checkTags(cpno);
            if (n > 0) {
                // 预留数量
                Float quantityTolist = gMterialsMapper.checkQuantityByUid(cpno) == null ? 0 : gMterialsMapper.checkQuantityByUid(cpno);
                // 贴纸数量
                float quantityTags = gMterialsMapper.getSumQuantity(cpno);
                int result = Float.compare(quantityTags, quantityTolist);
                if (result > 0) {
                    System.out.println(quantityTags + "==2==" + quantityTolist);
                    return Result.error("600", "该UID剩余绑定贴纸总数大于预留数量，不能扫UID下架所有贴纸，请扫描贴纸下架");
                } else if (result < 0) {
                    System.out.println(quantityTags + "==1==" + quantityTolist);
                    return Result.error("600", "该UID剩余绑定贴纸总数小于预留数量");
                } else if (result == 0) {
                    System.out.println("可以一键下架");
                    // 下架所有绑定的贴
                    int allStatus = gMterialsMapper.updateTagsStautsAll(cpno);
                    if (allStatus > 0) {
                        Inventory inventory = gMterialsMapper.getInventoryInfo(cpno);
                        gMterialsMapper.insertInventoryOut(inventory);
                        // 设置为已拣货
                        // 库存表删除下架的成品信息
                        gMterialsMapper.deleteiinventoryByUid(cpno);
                        // 更新TO明细表对应备货单为已拣货
                        gMterialsMapper.updateTono(cpno);

                    } else {
                        return Result.error("600", "更新贴纸失败");
                    }
                    // 更新拣货状态
                    int n1 = gMterialsMapper.checkStauts(cpno);
                    if (n1 > 0) {
                        // 更新TO管理表对应备货单为拣货中
                        gMterialsMapper.updateTosBHStatus(cpno, 1);
                    } else {
                        gMterialsMapper.updateTosBHStatus(cpno, 2);
                    }
                    return Result.success("所有贴纸出库成功");
                }
            } else {
                String YorN = gMaterialsService.checkQuantity(cpno);
                if ("UID不存在库存".equals(YorN))
                    return Result.error("600", "未找到该成品或该成品已拣货");

                if ("库存与备货数量不等".equals(YorN))
                    return Result.error("600", "该UID未拆分，请拆分UID数量与备货数量相等");

                String returnMessage = gMaterialsService.soldOut(cpno, role, toNo);
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

    public String soldOutCC4U(String toNo, String role, String cpno){

        float sum = 0;
        // 扫描箱号 -- 是否存在 并 找对应成品UID
        BoxInventory boxInventory = gMaterialsService.selectBox(cpno);
        System.out.println("CC4U");
        if (boxInventory != null) {

            System.out.println(boxInventory.getUid().toString());
            // 判UID是否有预留
            ToList toList = gMaterialsService.checkTolistUID(boxInventory.getUid().toString());
            if (toList == null) {
                System.out.println("111");
                // 根据备货单找所有UID对应的绑定的贴纸，如果存在与扫描的贴纸条件符合的则直接替换（可设置管理员权限），不符合则不能拣
                // 查询对应TO单是否存在同PN，贴纸同数量的数据
                List<BoxInventory> boxInventoryList = gMterialsMapper.getTolistAndBoxInfo(toNo, boxInventory.getPn().toString(), boxInventory.getCartonQty());
                if (boxInventoryList.size() > 0) {
                    System.out.println("222");
                    // boolean flagPO = tagsInventoryList.stream().filter(TagsInventory->TagsInventory.getPo().equals(tagsInventories.get(0).getPo())).findAny().isPresent();
                    BoxInventory boxInventory1 = new BoxInventory();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    boolean flag = false;
                    for (int i = 0;i < boxInventoryList.size();i++) {
                        boxInventory1 = boxInventoryList.get(i);
                        if (boxInventoryList.get(i).getPo().equals(boxInventory.getPo()) && sdf.format(boxInventoryList.get(i).getProductionDate()).equals(sdf.format(boxInventory.getProductionDate()))) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == true) {
                        System.out.println("允许替换操作");
                    } else if (!role.equals("manager")) {
                        return "替换贴纸日期或PO不相等，预留PO：" + boxInventory.getPo() + " 日期：" + sdf.format(boxInventory.getProductionDate()) + ",请切换管理员账号操作 N";
                    }
                    // 被替换UID已拣贴纸数量
                    long sumQuantity = gMterialsMapper.getSumcartonQty(boxInventory1.getUid().toString());
                    ToList toList1 = gMaterialsService.checkTolistUID(boxInventory1.getUid().toString());
                    if (sumQuantity + boxInventory.getCartonQty() > toList1.getQuantity())
                        return "替换后数量大于预留数量，不能替换 N";

                    // 更新替换和被替换的UID对应的贴纸信息
                    boxInventory.setStatus(1);
                    // 更新 -- 被替换
                    int n = gMterialsMapper.updateBoxInfo(boxInventory, boxInventory1);
                    if (n > 0) {
                        System.out.println("333");
                        // 更新替换
                        int n1 = gMterialsMapper.updateBoxInfo(boxInventory1, boxInventory);
                        if (n1 < 0)
                            return "替换信息修改失败2 N";

                    } else {
                        return "被替换信息修改失败1 N";
                    }
                    if ((sumQuantity + boxInventory.getCartonQty()) == toList1.getQuantity()) {
                        Inventory inventory = gMterialsMapper.getInventoryInfo(toList1.getUid().toString());
                        // 判断预留数量是否等于库存数量 （全出库/出一部分）
                        if (inventory.getUid_no() == toList1.getQuantity()) {
                            System.out.println("444");
                            // 预留 == 库存
                            // 将扫描的下架数据存到成品下架表
                            int n2 = gMterialsMapper.insertInventoryOut(inventory);
                            if (n2 <= 0) {
                                return "贴纸出库成功，成品下架失败1(下架表写入失败) N";
                            }
                            // 库存表删除下架的成品信息
                            int n3 = gMterialsMapper.deleteiinventoryByUid(toList1.getUid().toString());
                            if (n3 <= 0) {
                                return "贴纸出库成功，成品下架失败2(库存表删除失败) N";
                            }
                        } else {
                            System.out.println("555");
                            // 预留的数量小于库存数量
                            System.out.println("33");
                            float uidNo = inventory.getUid_no();
                            inventory.setUid_no(sumQuantity);
                            inventory.setTagsQuantity(sumQuantity);
                            int n6 = gMterialsMapper.insertInventoryOut(inventory);
                            if (n6 <= 0) {
                                return "贴纸出库成功，成品部分下架失败1(下架表写入失败) N";
                            }
                            // 库存表删除下架的成品信息
                            int n3 = gMterialsMapper.deleteiinventoryByUid(inventory.getUid().toString());
                            if (n3 <= 0) {
                                return "贴纸出库成功，成品下架失败2(库存表删除失败) N";
                            }

                            // 新产生一个UID存放剩余的绑定的贴纸信息
                            Inventory inventory1 = new Inventory();
                            String uid = getGuid();
                            BeanUtil.copyProperties(inventory, inventory1);
                            System.out.println("库存赋值(替换)：" + inventory1);
                            inventory1.setUid(uid);
                            inventory1.setUid_no(uidNo - sumQuantity);
                            inventory1.setStatus(1);

                            // 将剩余的贴纸信息放到新产生的库存UID中
                            List<BoxInventory> boxInventoryList1 = gMterialsMapper.checkBoxByUID(inventory.getUid().toString());
                            System.out.println("测试:" + boxInventoryList1.toString());
                            for (BoxInventory boxInventory2 : boxInventoryList1) {
                                boxInventory2.setUid(inventory1.getUid().toString());
                            }
                            inventory1.setTagsQuantity((long) (uidNo - sumQuantity));
                            int n7 = gMterialsMapper.insertInventory(inventory1);
                            if (n7 <= 0)
                                return "贴纸出库成功，新增UID失败 N";

                            System.out.println(boxInventoryList1.toString());
                            int n8 = gMterialsMapper.updateboxInfo1(boxInventoryList1);
                            if (n8 < 0)
                                return "剩余贴纸转移失败 N";

                        }
                        int n4 = gMterialsMapper.updateTono(toList1.getUid().toString());
                        if (n4 <= 0)
                            return "贴纸出库后对应成品UID出库失败 N";

                        // 查询即存在已拣货和未拣货备货单（TO明细表）
                        int n5 = gMterialsMapper.checkStauts(toList1.getUid().toString());
                        if (n5 > 0) {
                            // 更新TO管理表对应备货单为拣货中
                            gMterialsMapper.updateTosBHStatus(toList1.getUid().toString(), 1);
                        } else {
                            gMterialsMapper.updateTosBHStatus(toList1.getUid().toString(), 2);
                        }
                    }
                    return "贴纸替换成功,剩余出库数量：" + (toList1.getQuantity() - (sumQuantity + boxInventory.getCartonQty()));

                    // boolean flagDate = tagsInventoryList.stream().anyMatch(TagsInventory -> TagsInventory.getProductionDate().equals(tagsInventories.get(0).getProductionDate()));

                } else {
                    System.out.println("预留的TO单不存在可替换的贴纸成品");
                    return "预留的TO单不存在可替换的贴纸成品 N";
                }
            } else {
                // UID被预留，但不是当前备货单预留，不能执行替换操作
                if (!toList.getToNo().toString().equals(toNo))
                    return "该贴纸被其它备货单(" + toList.getToNo().toString() + ")预留，不能出库 N";

                // 根据预留UID查询库存信息
                Inventory inventory = gMterialsMapper.getInventoryInfo(toList.getUid().toString());
                // 预留数量
                float quantity_tolist = toList.getQuantity();
                // 库存数量大于预留数量
//                    if (Float.compare(inventory.getUid_no(), quantity_tolist) > 0) {
//                        System.out.println(inventory.getUid_no() + "===" + quantity_tolist);
//                        return Result.error("600", "该贴纸对应UID实际绑定贴纸数量为" + inventory.getUid_no() + "，应该出数量为" + quantity_tolist + ",请拆分UID数量与备货数量相等（扫贴纸提示）");
//                    }
                System.out.println("22");
                // 查询已拣的贴纸数量
                long sumQuantity = gMterialsMapper.getSumcartonQty(boxInventory.getUid().toString());
                sum = (float) sumQuantity + (float) boxInventory.getCartonQty();
                // 比较预留数量和已拣贴纸数量
                int result = Float.compare(toList.getQuantity(), sum);
                if (result == 0) {
                    // 预留数量等于 已拣贴纸数量+当前扫描的贴纸数量
                    System.out.println(boxInventory.getCartonNo());
                    // 更新当前扫描贴纸状态
                    int n1 = gMaterialsService.updateBoxStauts(boxInventory.getCartonNo().toString());
                    if (n1 <= 0)
                        return "贴纸箱号出库失败 N";

                    // 判断预留数量是否等于库存数量 （全出库/出一部分）
                    if (inventory.getUid_no() == sum) {
                        System.out.println("555");
                        // 判断该贴纸UID是否还存在未出库批次
//                            List<TagsInventory> tagsInventoryList = gMterialsMapper.checkTagsByUID(inventory.getUid().toString());
//                            if (tagsInventoryList.size() > 0) {
//                                // 查询拆分后的UID
//                                String SplitUID = gMterialsMapper.getSplitUID(inventory.getUid().toString()) == null ? "" : gMterialsMapper.getSplitUID(inventory.getUid().toString());
//                                if (!"".equals(SplitUID)) {
//                                    // 将剩余的贴纸信息插入到拆分后的UID中
//                                    System.out.println("待写完。。。");
//
//                                }
//                            }
                        // 预留 == 库存
                        // 将扫描的下架数据存到成品下架表
                        int n2 = gMterialsMapper.insertInventoryOut(inventory);
                        if (n2 <= 0) {
                            return "贴纸出库成功，成品下架失败1(下架表写入失败) N";
                        }
                        // 库存表删除下架的成品信息
                        int n3 = gMterialsMapper.deleteiinventoryByUid(toList.getUid().toString());
                        if (n3 <= 0) {
                            return "贴纸出库成功，成品下架失败2(库存表删除失败) N";
                        }
                    } else {
                        System.out.println("666");
                        // 预留的数量小于库存数量
                        System.out.println("33");
                        float uidNo = inventory.getUid_no();
                        inventory.setUid_no(sum);
                        int n6 = gMterialsMapper.insertInventoryOut(inventory);
                        if (n6 <= 0) {
                            return "贴纸出库成功，成品部分下架失败1(下架表写入失败) N";
                        }
                        // 库存表删除下架的成品信息
                        int n3 = gMterialsMapper.deleteiinventoryByUid(inventory.getUid().toString());
                        if (n3 <= 0) {
                            return "贴纸出库成功，成品下架失败2(库存表删除失败) N";
                        }

                        // 新产生一个UID存放剩余的绑定的贴纸信息
                        Inventory inventory1 = new Inventory();
                        String uid = getGuid();
                        BeanUtil.copyProperties(inventory, inventory1);
                        System.out.println("库存赋值：" + inventory1);
                        inventory1.setUid(uid);
                        inventory1.setUid_no(uidNo - sum);
                        inventory1.setStatus(1);
                        // int n7 = gMterialsMapper.updateQuantityStauts(inventory.getUid().toString(), (long) (inventory.getUid_no() - sum), 1);
                        // 将剩余的贴纸信息放到新产生的库存UID中
                        List<BoxInventory> boxInventoryList = gMterialsMapper.checkBoxByUID(inventory.getUid().toString());
                        System.out.println("1121" + boxInventoryList);
                        for (BoxInventory boxInventory1 : boxInventoryList) {
                            boxInventory1.setUid(inventory1.getUid().toString());
                        }
                        inventory1.setTagsQuantity((long) (uidNo - sum));
                        System.out.println("数量" + inventory1.toString());
                        int n7 = gMterialsMapper.insertInventory(inventory1);
                        if (n7 <= 0)
                            return "贴纸出库成功，新增UID失败 N";

                        System.out.println("1212" + boxInventoryList);
                        int n8 = gMterialsMapper.updateboxInfo1(boxInventoryList);
                        if (n8 < 0)
                            return "剩余贴纸转移失败 N";

                    }
                    // 更新预留表预留的UID装维为已拣货
                    int n4 = gMterialsMapper.updateTono(toList.getUid().toString());
                    if (n4 <= 0)
                        return "贴纸出库后对应成品UID出库失败 N";

                    // 查询即存在已拣货和未拣货备货单（TO明细表）
                    int n5 = gMterialsMapper.checkStauts(toList.getUid().toString());
                    if (n5 > 0) {
                        // 更新TO管理表对应备货单为拣货中
                        gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
                    } else {
                        gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
                    }

                    return "成品出库成功";

                } else if (result > 0) {
                    System.out.println("777");
                    // 预留数量比已拣数量大，则允许继续扫描贴纸出库（拣货）
                    System.out.println(boxInventory.getCartonNo());
                    // 更新贴纸状态为已拣货
                    int n = gMaterialsService.updateBoxStauts(boxInventory.getCartonNo().toString());
                    if (n <= 0)
                        return "贴纸出库失败！ N";

                    // 查询即存在已拣货和未拣货备货单（TO明细表）
//                        int n1 = gMterialsMapper.checkStauts(toList.getUid().toString());
//                        if (n1 > 0) {
//                            // 更新TO管理表对应备货单为拣货中
//                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 1);
//                        } else {
//                            gMterialsMapper.updateTosBHStatus(toList.getUid().toString(), 2);
//                        }
                    return "贴纸出库成功！剩余未拣贴纸数量" + (quantity_tolist - sum);

                } else if (result < 0) {
                    // 已拣贴纸数量加当前扫描的贴纸数量大于预留数量
                    System.out.println(toList.getQuantity() + "==" + sum);
                    return "出库数量大于应出库数量，应出库" + (sum - (sum - toList.getQuantity())) + " N";
                }
            }
        } else {
            return "不存在相关贴纸，或该贴纸已出库！ N";
        }
        return "N";
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
     * 根据日期下载走货单（无车情况）
     * */
    @ResponseBody
    @PostMapping(value = "/downloadshipmentno2")
    public Result downloadShipmentNo2(@RequestBody String date) {

        List<String> list = gMaterialsService.downloadShipmentNo2(date);
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
    public Result updateToNo(@RequestBody JSONObject params) throws MessagingException, IOException {

        JSONObject nodes = new JSONObject(params.getStr("params"));
        String shipmentno = nodes.getStr("shipmentno");
        System.out.println(shipmentno);
        String date = nodes.getStr("date");
        date.replace("-", "");

        String returnMessage = gMaterialsService.updateToNo(date, shipmentno);

//        SAPUtil sapUtil = new SAPUtil();
//        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS_1(startDate, startDate);
//        // 实体类对象存到List集合中，统计对象中某个值相同的数据
//
//        for (int i = 0;i < list.size();i++) {
//            if (list.get(i).getLastComfirm() != null && (list.get(i).getLastComfirm().equals("船务") || list.get(i).getLastComfirm().equals("货仓"))) {
//                System.out.println(list.get(i).toString());
//            }
//        }

        return null;
    }

    /**
     * 生成新UID;
     * 新UID格式： FG + 年份 + 时间戳（从第三位开始，包含第三位） + 自定义ran（100开始+=1,999结束）
     *
     * @return String
     */
    public String getGuid() {
        GMaterailsController.Guid += 1;
        // 获取时间戳
        long now = System.currentTimeMillis();
        System.out.println(now);
        // 获取4位年份数字
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        // 获取年份
        String time = dateFormat.format(now);
        System.out.println(time);
        String info = now + "";
        int ran = 0;
        if (GMaterailsController.Guid > 999) {
            GMaterailsController.Guid = 100;
        }
        ran = GMaterailsController.Guid;

        // 新UID格式： FG + 年份 + 时间戳（从第三位开始，包含第三位） + 自定义ran（100开始+=1,999结束）
        return "FG" + time + info.substring(2, info.length()) + ran;
    }

}
