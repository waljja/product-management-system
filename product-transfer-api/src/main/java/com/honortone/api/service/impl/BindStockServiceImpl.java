package com.honortone.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honortone.api.entity.*;
import com.honortone.api.mapper.BindStockMapper;
import com.honortone.api.mapper.GMterialsMapper;
import com.honortone.api.mapper.InStockMapper;
import com.honortone.api.mapper.SaveInfoMapper;
import com.honortone.api.service.BindStockService;
import com.honortone.api.service.SaveInfoService;
import com.honortone.api.utils.SAPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 绑库接口实现类
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-17
 */

@Service
public class BindStockServiceImpl extends ServiceImpl<BindStockMapper, Inventory> implements BindStockService {

    @Resource
    private InStockMapper inStockMapper;

    @Autowired
    private BindStockMapper bindStockMapper;

    @Autowired
    private GMterialsMapper gMterialsMapper;

    @Autowired
    private SaveInfoMapper saveInfoMapper;

    @Override
    public int checkUID(String uid) {
        return bindStockMapper.checkUID(uid);
    }

    @Override
    public int checkUID2(String uid) {
        return bindStockMapper.checkUID2(uid);
    }

    @Override
    public String bindStock_1(String uid, String stock, String username) {

        String returnMessage = "";
        CheckList checkList = inStockMapper.ifSuccess(uid);
        if (checkList == null)
            return "未找到该UID信息，请检查是否输入错误!";

        System.out.println("1112u" + uid);
        System.out.println(checkList);

        returnMessage = checkInstockAndBindstock(checkList, stock, username);

        return returnMessage;

    }

    @Override
    public String bindStock_2(String uid, String tz, String stock, String htpn, String khpn, String rectime, long qty, String clientBatch, String username) throws ParseException {

        String returnMessage = "";
        CheckList checkList = inStockMapper.ifSuccess(uid);
        if (checkList == null)
            return "未查询到该UID信息，请检查是否输入错误";

        System.out.println("ceimpl" + tz);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_checklist = "";
        String date_box = "";
        SAPUtil sapUtil = new SAPUtil();
        if (bindStockMapper.checkInstock(checkList) > 0) {
            Inventory inventory = new Inventory();
            if (htpn != "" && htpn != null && htpn.equals(checkList.getPn().toString())) {
                BeanUtil.copyProperties(checkList, inventory, true);
                // 因为checkList没有stock字段可赋值给inventoryDto
//                inventory.setStock(stock);
//                String uid_id = bindStockMapper.getUidId(uid);
//                inventory.setUid_id(uid_id);
                int n1 = bindStockMapper.toinsert(inventory);  //临时注释：是否需要判断贴纸是否存在，htpn\khpn是否需要保存
                if (n1 > 0)
                    returnMessage = "HT贴纸绑库成功";

            } else if (khpn != "" && khpn != null) {
                // 根据扫描的贴纸调sap接口返回htpn
                // 获取htpn后重复上一个步骤（判htpn跟送检单pn是否一致，一致则绑库）
                List<String[]> list1 = sapUtil.getHt_PnByKh_Pn(khpn, htpn);
                int n = list1.size();
                System.out.println(list1.toString());
                int i = 0;
                for (String[] arr1 : list1) {
                    // 数组转为集合，使用 contains方法判断是否存在htpn与成品单PN相等
                    if (Arrays.asList(arr1[1]).contains(checkList.getPn().toString())) {
                        break;
                    }
                    i += 1;
                }
                if (i == n) {
                    returnMessage = "送检单型号与客户贴纸型号不一致";
                } else {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    BeanUtil.copyProperties(checkList, inventory, true);
                    inventory.setStock(stock);
                    inventory.setQa_sign(username);
                    // 暂用不上
                    String uid_id = bindStockMapper.getUidId(uid);
                    TagsInventory tagsInventory = new TagsInventory();
                    BeanUtil.copyProperties(inventory, tagsInventory, true);
                    tagsInventory.setClientBatch(clientBatch);
                    tagsInventory.setClientPn(khpn);
                    tagsInventory.setQuantity(qty);
                    String rectime1 = rectime + " 00:00:00";
                    Date date = sdf1.parse(rectime1);
                    tagsInventory.setProductionDate(date);

                    // 过账编号50开头的不管控 过账日期
                    if (checkList.getSap101().substring(0, 2).equals("50")) {
                        // 是否存在该uid
                        Inventory inventory1 = bindStockMapper.checkInventory(inventory.getUid().toString());
                        long tagsSum = bindStockMapper.checktagsSum(inventory.getUid().toString());
                        if (inventory1 != null) {
                            if ((tagsSum + tagsInventory.getQuantity()) > inventory1.getUid_no()) {
                                System.out.println();
                                return "绑定贴纸总数大于成品单打印数量，不允许打印";
                            } else {
                                if (bindStockMapper.checkTags(clientBatch) > 0) {
                                    returnMessage = "该贴纸 批次已绑定过";
                                } else {
                                    // 保存贴纸信息
                                    int n1 = bindStockMapper.insertTagsInventory(tagsInventory);
                                    if (n1 > 0) {
                                        inventory1.setTagsQuantity(tagsSum + tagsInventory.getQuantity());
                                        bindStockMapper.updateInventory(inventory1);
                                        if (inventory1.getUid_no() - (tagsSum + tagsInventory.getQuantity()) == 0 || inventory1.getUid_no() - (tagsSum + tagsInventory.getQuantity()) == 0.0) {
                                            returnMessage = "该贴纸完成绑定";
                                        } else {
                                            returnMessage = "珠飞客户贴纸成功，还剩" + (inventory1.getUid_no() - (tagsSum + tagsInventory.getQuantity())) + "数量需要绑定";
                                        }
                                    }
                                }
                            }
                        } else {
                            // 第一次绑库，设置两个PO
                            List<String[]> list = new ArrayList<>();
                            list = sapUtil.getPoInfo(inventory.getPn());
                            String s = "";
                            // 获取上架PO对应的采购人PO
                            for (String[] arr : list) {
                                if (Arrays.asList(arr).contains(checkList.getPo().toString())) {
                                    if (arr[2] != null && !"".equals(arr[2])) {
                                        // 如果上架是“收货人採購單號碼”则赋值“客戶採購單號碼 ”给clientPO，反之 一样
                                        if (arr[2].equals(inventory.getPo())) {
                                            s = arr[1];
                                        } else {
                                            s = arr[2];
                                        }
                                    }
                                    break;
                                }
                            }
                            System.out.println("65000" + s);
                            // 设置客户PO （该PO为收货人采购单号）
                            inventory.setClientPO(s);
                            // inventory.setUid_no(qty);
                            // inventory.setUid_id(uid_id);
                            long uidNo = (long) inventory.getUid_no();
                            if (tagsInventory.getQuantity() <= uidNo) {
                                System.out.println(tagsInventory.getQuantity() + "<=" + uidNo);
                                inventory.setTagsQuantity(tagsInventory.getQuantity());
                                // 保存绑库UID
                                int n2 = bindStockMapper.toinsert(inventory);
                                if (n2 > 0) {
                                    if (bindStockMapper.checkTags(clientBatch) > 0) {
                                        returnMessage = "该贴纸 批次已绑定过";
                                    } else {
                                        // 保存贴纸信息
                                        int n3 = bindStockMapper.insertTagsInventory(tagsInventory);
                                        if (n3 > 0) {
                                            if (uidNo - tagsInventory.getQuantity() == 0) {
                                                returnMessage = "该贴纸完成绑定";
                                            } else {
                                                returnMessage = "珠飞客户贴纸成功,还剩" + (uidNo - tagsInventory.getQuantity()) + "数量需要绑定";
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println(tagsInventory.getQuantity() + ">" + uidNo);
                                return "贴纸数量大于成品单打印数量";
                            }
                        }
                    } else if (rectime.equals(sdf.format(inventory.getProduction_date()))) {
                        // 是否存在该uid
                        Inventory inventory1 = bindStockMapper.checkInventory(inventory.getUid().toString());
                        long tagsSum = bindStockMapper.checktagsSum(inventory.getUid().toString());
                        if (inventory1 != null) {
                            if ((tagsSum + tagsInventory.getQuantity()) > inventory1.getUid_no()) {
                                System.out.println();
                                return "绑定贴纸总数大于成品单打印数量，不允许打印";
                            } else {
                                if (bindStockMapper.checkTags(clientBatch) > 0) {
                                    returnMessage = "该贴纸 批次已绑定过";
                                } else {
                                    // 保存贴纸信息
                                    int n1 = bindStockMapper.insertTagsInventory(tagsInventory);
                                    if (n1 > 0) {
                                        inventory1.setTagsQuantity(tagsSum + tagsInventory.getQuantity());
                                        bindStockMapper.updateInventory(inventory1);
                                        if (inventory1.getUid_no() - (tagsSum + tagsInventory.getQuantity()) == 0 || inventory1.getUid_no() - (tagsSum + tagsInventory.getQuantity()) == 0.0) {
                                            returnMessage = "该贴纸完成绑定";
                                        } else {
                                            returnMessage = "珠飞客户贴纸成功，还剩" + (inventory1.getUid_no() - (tagsSum + tagsInventory.getQuantity())) + "数量需要绑定";
                                        }
                                    }
                                }
                            }
                        } else {
                            // 第一次绑库，设置两个PO
                            List<String[]> list = new ArrayList<>();
                            list = sapUtil.getPoInfo(inventory.getPn());
                            String s = "";
                            // 获取上架PO对应的采购人PO
                            for (String[] arr : list) {
                                if (Arrays.asList(arr).contains(checkList.getPo().toString())) {
                                    if (arr[2] != null && !"".equals(arr[2])) {
                                        // 如果上架是“收货人採購單號碼”则赋值“客戶採購單號碼 ”给clientPO，反之 一样
                                        if (arr[2].equals(inventory.getPo())) {
                                            s = arr[1];
                                        } else {
                                            s = arr[2];
                                        }
                                    }
                                    break;
                                }
                            }
                            System.out.println("65000" + s);
                            // 设置客户PO （该PO为收货人采购单号）
                            inventory.setClientPO(s);
                            // inventory.setUid_no(qty);
                            // inventory.setUid_id(uid_id);
                            long uidNo = (long) inventory.getUid_no();
                            if (tagsInventory.getQuantity() <= uidNo) {
                                System.out.println(tagsInventory.getQuantity() + "<=" + uidNo);
                                inventory.setTagsQuantity(tagsInventory.getQuantity());
                                // 保存绑库UID
                                int n2 = bindStockMapper.toinsert(inventory);
                                if (n2 > 0) {
                                    if (bindStockMapper.checkTags(clientBatch) > 0) {
                                        returnMessage = "该贴纸 批次已绑定过";
                                    } else {
                                        // 保存贴纸信息
                                        int n3 = bindStockMapper.insertTagsInventory(tagsInventory);
                                        if (n3 > 0) {
                                            if (uidNo - tagsInventory.getQuantity() == 0) {
                                                returnMessage = "该贴纸完成绑定";
                                            } else {
                                                returnMessage = "珠飞客户贴纸成功,还剩" + (uidNo - tagsInventory.getQuantity()) + "数量需要绑定";
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println(tagsInventory.getQuantity() + ">" + uidNo);
                                return "贴纸数量大于成品单打印数量";
                            }
                        }
                    } else {
                        return "珠飞贴纸日期与成品单101过账日期不相等，且过账编号非50开头，不允许绑定";
                    }
                }
            } else if (!tz.contains("/") && !tz.contains("@")) {

                System.out.println(tz);
                BoxInventory boxInventory = bindStockMapper.getBoxInfo(tz);
                date_checklist = sdf.format(checkList.getProduction_date());
//                System.out.println(boxInventory.getCreateTime());
//                System.out.println(boxInventory.getProductionDate());
                date_box = sdf.format(boxInventory.getProductionDate());
                if (checkList.getSap101().substring(0, 2).equals("50") && boxInventory != null && boxInventory.getPn().toString().equals(checkList.getPn().toString()) && boxInventory.getPo().toString().equals(checkList.getPo().toString())) {
                    BeanUtil.copyProperties(checkList, inventory, true);
                    inventory.setStock(stock);
                    BeanUtil.copyProperties(inventory, boxInventory, true);
                    // 是否存在该uid
                    Inventory inventory1 = bindStockMapper.checkInventory(inventory.getUid().toString());
                    if (inventory1 != null) {
                        long sumqty = bindStockMapper.checkBoxQuantity(inventory1.getUid().toString());
                        if (sumqty >= inventory1.getUid_no() || (sumqty + boxInventory.getCartonQty()) > inventory1.getUid_no()) {
                            returnMessage = "绑定箱子数量大于成品单数量";
                        } else {
                            // 保存贴纸信息
                            int n3 = bindStockMapper.insertBoxInventory(boxInventory);
                            if (n3 > 0) {
                                inventory1.setTagsQuantity(sumqty + boxInventory.getCartonQty());
                                bindStockMapper.updateInventory(inventory1);
                                if (inventory1.getUid_no() - (sumqty + boxInventory.getCartonQty()) == 0 || inventory1.getUid_no() - (sumqty + boxInventory.getCartonQty()) == 0.0) {
                                    returnMessage = "该贴纸完成绑定";
                                } else {
                                    returnMessage = "CC4U客户贴纸成功,还剩" + (inventory1.getUid_no() - (sumqty + boxInventory.getCartonQty())) + "数量需绑定";
                                }
                            }
                        }
                    } else {
                        // 第一次绑库，设置两个PO
                        List<String[]> list = new ArrayList<>();
                        list = sapUtil.getPoInfo(inventory.getPn());
                        String s = "";
                        // 获取上架PO对应的采购人PO
                        for (String[] arr : list) {
                            if (Arrays.asList(arr).contains(checkList.getPo().toString())) {
                                if (arr[2] != null && !"".equals(arr[2])) {
                                    // 如果上架是“收货人採購單號碼”则赋值“客戶採購單號碼 ”给clientPO，反之 一样
                                    if (arr[2].equals(inventory.getPo())) {
                                        s = arr[1];
                                    } else {
                                        s = arr[2];
                                    }
                                }
                                break;
                            }
                        }
                        System.out.println("65000" + s);
                        // 设置客户PO （该PO为收货人采购单号）
                        inventory.setClientPO(s);
                        if (boxInventory.getCartonQty() > inventory.getUid_no()) {
                            System.out.println(boxInventory.getCartonQty() + ">" + inventory.getUid_no());
                            returnMessage = "绑定箱子数量大于成品单数量";
                        } else {
                            System.out.println(boxInventory.getCartonQty() + "<=" + inventory.getUid_no());
                            // 保存绑库UID
                            inventory.setTagsQuantity(boxInventory.getCartonQty());
                            int n2 = bindStockMapper.toinsert(inventory);
                            if (n2 > 0) {
                                if (bindStockMapper.checkBox(boxInventory.getCartonNo().toString()) > 0) {
                                    returnMessage = "该箱号已绑定过";
                                } else {
                                    int n3 = bindStockMapper.insertBoxInventory(boxInventory);
                                    if (n3 > 0) {
                                        if (inventory.getUid_no() - boxInventory.getCartonQty() == 0) {
                                            returnMessage = "该贴纸完成绑定";
                                        } else {
                                            returnMessage = "CC4U客户贴纸成功，还剩" + (inventory.getUid_no() - boxInventory.getCartonQty()) + "数量需绑定";
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (boxInventory != null && boxInventory.getPn().toString().equals(checkList.getPn().toString()) && boxInventory.getPo().toString().equals(checkList.getPo().toString()) && date_checklist.equals(date_box)) {

                    BeanUtil.copyProperties(checkList, inventory, true);
                    inventory.setStock(stock);
                    BeanUtil.copyProperties(inventory, boxInventory, true);
                    // 是否存在该uid
                    Inventory inventory1 = bindStockMapper.checkInventory(inventory.getUid().toString());
                    if (inventory1 != null) {
                        long sumqty = bindStockMapper.checkBoxQuantity(inventory1.getUid().toString());
                        if (sumqty >= inventory1.getUid_no() || (sumqty + boxInventory.getCartonQty()) > inventory1.getUid_no()) {
                            returnMessage = "绑定箱子数量大于成品单数量";
                        } else {
                            // 保存贴纸信息
                            int n3 = bindStockMapper.insertBoxInventory(boxInventory);
                            if (n3 > 0) {
                                inventory1.setTagsQuantity(sumqty + boxInventory.getCartonQty());
                                bindStockMapper.updateInventory(inventory1);
                                if (inventory1.getUid_no() - (sumqty + boxInventory.getCartonQty()) == 0 || inventory1.getUid_no() - (sumqty + boxInventory.getCartonQty()) == 0.0) {
                                    returnMessage = "该贴纸完成绑定";
                                } else {
                                    returnMessage = "CC4U客户贴纸成功,还剩" + (inventory1.getUid_no() - (sumqty + boxInventory.getCartonQty())) + "数量需绑定";
                                }
                            }
                        }
                    } else {
                        // 第一次绑库，设置两个PO
                        List<String[]> list = new ArrayList<>();
                        list = sapUtil.getPoInfo(inventory.getPn());
                        String s = "";
                        // 获取上架PO对应的采购人PO
                        for (String[] arr : list) {
                            if (Arrays.asList(arr).contains(checkList.getPo().toString())) {
                                if (arr[2] != null && !"".equals(arr[2])) {
                                    // 如果上架是“收货人採購單號碼”则赋值“客戶採購單號碼 ”给clientPO，反之 一样
                                    if (arr[2].equals(inventory.getPo())) {
                                        s = arr[1];
                                    } else {
                                        s = arr[2];
                                    }
                                }
                                break;
                            }
                        }
                        System.out.println("65000" + s);
                        // 设置客户PO （该PO为收货人采购单号）
                        inventory.setClientPO(s);
                        if (boxInventory.getCartonQty() > inventory.getUid_no()) {
                            System.out.println(boxInventory.getCartonQty() + ">" + inventory.getUid_no());
                            returnMessage = "绑定箱子数量大于成品单数量";
                        } else {
                            System.out.println(boxInventory.getCartonQty() + "<=" + inventory.getUid_no());
                            // 保存绑库UID
                            inventory.setTagsQuantity(boxInventory.getCartonQty());
                            int n2 = bindStockMapper.toinsert(inventory);
                            if (n2 > 0) {
                                if (bindStockMapper.checkBox(boxInventory.getCartonNo().toString()) > 0) {
                                    returnMessage = "该箱号已绑定过";
                                } else {
                                    int n3 = bindStockMapper.insertBoxInventory(boxInventory);
                                    if (n3 > 0) {
                                        if (inventory.getUid_no() - boxInventory.getCartonQty() == 0) {
                                            returnMessage = "该贴纸完成绑定";
                                        } else {
                                            returnMessage = "CC4U客户贴纸成功，还剩" + (inventory.getUid_no() - boxInventory.getCartonQty()) + "数量需绑定";
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    returnMessage = "不存在箱号或该箱号PN、PO、包装时间与成品单PN、PO、日期不一致！";
                }
            } else {
                returnMessage = "送检单型号与客户贴纸型号不一致或客户贴纸为空";
            }
        } else {
            returnMessage = "未收货/转数失败，不能绑库";
        }
        return returnMessage;
    }

//    @Override
//    public String bindStock_2(String uid, String tz, String stock, String htpn, String khpn, String rectime) {
//
//        String returnMessage = "";
//        CheckList checkList = inStockMapper.ifSuccess(uid);
//        SAPUtil sapUtil = new SAPUtil();
//        if (bindStockMapper.checkInstock(checkList) > 0) {
//            Inventory inventory = new Inventory();
//            if (htpn != "" && htpn != null && htpn.equals(checkList.getPn().toString())) {
//                BeanUtil.copyProperties(checkList, inventory, true);
//                // 因为checkList没有stock字段可赋值给inventoryDto
//                inventory.setStock(stock);
//                String uid_id = bindStockMapper.getUidId(uid);
//                inventory.setUid_id(uid_id);
//                int n1 = bindStockMapper.toinsert(inventory);  //临时注释：是否需要判断贴纸是否存在，htpn\khpn是否需要保存
//                if (n1 > 0)
//                    returnMessage = "贴纸绑库成功";
//
//            } else if (khpn != "" && khpn != null) {
//                // 根据扫描的贴纸调sap接口返回htpn
//                // 获取htpn后重复上一个步骤（判htpn跟送检单pn是否一致，一致则绑库）
//                List<String[]> list1 = sapUtil.getHt_PnByKh_Pn(khpn, htpn);
//                int n = list1.size();
//                System.out.println(list1.toString());
//                int i = 0;
//                for (String[] arr1 : list1) {
//                    // 数组转为集合，使用 contains方法判断是否存在htpn与成品单PN相等
//                    if (Arrays.asList(arr1[1]).contains(checkList.getPn().toString())) {
//                        break;
//                    }
//                    i += 1;
//                }
//                if (i == n) {
//                    returnMessage = "送检单型号与客户贴纸型号不一致";
//                } else {
//                    BeanUtil.copyProperties(checkList, inventory, true);
//                    inventory.setStock(stock);
//                    String uid_id = bindStockMapper.getUidId(uid);
//                    inventory.setUid_id(uid_id);
//                    int n1 = bindStockMapper.toinsert(inventory);
//                    if (n1 > 0)
//                        returnMessage = "贴纸绑库成功";
//                }
//            } else {
//                returnMessage = "送检单型号与客户贴纸型号不一致或客户贴纸为空";
//            }
//        } else {
//            returnMessage = "未收货/转数失败，不能绑库";
//        }
//        return returnMessage;
//    }


    /// 这条插入的是不需要的，只是为了记录让成品送检单和贴纸产生关联，后期计算有多少贴纸时，需-1（即，把这条不需要的记录减掉，则为实际【一个送检单下有多少个贴纸】，或者判断库位号部位null）
    @Override
    public String firstInsert(String uid) {

        String returnMessage = "";
        CheckList checkList = inStockMapper.ifSuccess(uid);

        if (bindStockMapper.checkInstock(checkList) > 0) {
            Inventory inventory = new Inventory();
            BeanUtil.copyProperties(checkList, inventory, true);
            List<Inventory> list1 = bindStockMapper.checkInfoByUid_2(uid);
            if (list1.size() > 0) {
                returnMessage = "已存在，请继续扫描";
            } else {
                int n = bindStockMapper.firstInsert(inventory);
                if (n > 0) {
                    returnMessage = "扫描贴纸前插入成功";
                } else {
                    returnMessage = "扫描贴纸前插入失败";
                }
            }
        } else {
            returnMessage = "转数失败/未收货，不能绑库";
        }

        return returnMessage;
    }

    /**
     * 回仓
     * */
    @Override
    public String RollBack(String uid, String rollbackReason) {

        CheckList checkList = bindStockMapper.checkOldUid(uid);
        String returnMessage = "";
        // 没有旧UID 表示退回数量与原UID数量相等
        if (checkList != null && checkList.getOld_uid() != null) {
            // 旧UID数据
            ToList toList = bindStockMapper.getUidInfo(checkList.getOld_uid().toString());
            // 扫描的UID数据
            ToList toList1 = bindStockMapper.getUidInfo(uid);
            if (toList != null && toList.getUid() != null) {
                CheckList checkList1 = bindStockMapper.checkOldUid(toList.getUid().toString());
                if ((checkList.getUid_no() + checkList1.getUid_no()) == toList.getQuantity()) {
                    // 新拆分（插入库存表）的数据是否需要重新收货？
                    Inventory inventory = new Inventory();
                    inventory.setUid(uid);
                    inventory.setPn(toList.getPn().toString());
                    inventory.setPo(toList.getPo().toString());
                    inventory.setStock(toList.getStock().toString());
                    inventory.setBatch(toList.getBatch().toString());
                    inventory.setUid_no(checkList.getUid_no());
                    inventory.setStatus(1);
                    inventory.setWo(checkList.getWo().toString());
                    inventory.setProduction_date(checkList.getProduction_date());
                    inventory.setQa_sign(checkList.getQa_sign().toString());
                    inventory.setRollbackReason(rollbackReason);
                    bindStockMapper.toinsert(inventory);
                    // 对应UID数量
                    long qty = (long) (toList.getQuantity() - checkList.getUid_no());
                    // 对应备货单总数
                    long qty1 = (long) (toList.getBatchQty() - checkList.getUid_no());
                    bindStockMapper.updateQuantityAndStatus(qty, toList.getUid().toString());
                    gMterialsMapper.updateTosListQuantity(toList.getToNo().toString(), qty1);
                    bindStockMapper.updateTosStatus(toList.getToNo().toString(), qty1);
                    returnMessage = "回仓成功";
                } else {
                    return "该UID不是需拆分UID所拆分，不能执行回仓操作";
                }
            } else if (toList1.getUid() != null) {

                Inventory inventory = bindStockMapper.getInventoryInfo(toList1);
                inventory.setRollbackReason(rollbackReason);
                System.out.println(inventory);
                bindStockMapper.toinsert(inventory);
                bindStockMapper.deleteTolistInfo(uid);
                // 将待回仓状态设置为已拣货
                int n = bindStockMapper.updateTosStatus(toList1.getToNo().toString(),toList.getBatchQty());
                if (n > 0) {
                    returnMessage = "回仓成功";
                } else {
                    returnMessage = "回仓失败";
                }
            } else {
                return "该UID不能执行回仓操作";
            }
        } else {
            ToList toList = bindStockMapper.getUidInfo(uid);
            System.out.println(toList.getUid());
            if (toList == null)
                return "该UID不存在备货单";

            Inventory inventory = bindStockMapper.getInventoryInfo(toList);
            if (inventory == null)
                return "该UID找不到相关库存信息";

            inventory.setRollbackReason(rollbackReason);
            System.out.println(inventory);
            bindStockMapper.toinsert(inventory);
            bindStockMapper.deleteTolistInfo(uid);
            // 将待回仓状态设置为已拣货
            int n = bindStockMapper.updateTosStatus(toList.getToNo().toString(),toList.getBatchQty());
            if (n > 0) {
                returnMessage = "回仓成功";
            } else {
                returnMessage = "回仓失败";
            }
        }
        return returnMessage;
    }

    @Override
    public int updateTosStock(String toNo, String stock) {

        FgShipmentInfo fgShipmentInfo = bindStockMapper.getShipmentInfoBytoNo(toNo);
        // Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDate = simpleDateFormat.format(fgShipmentInfo.getShipmentDate());

        SAPUtil sapUtil = new SAPUtil();
        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS_1(startDate, startDate);
        // 存放船务确认信息
        List<FgShipmentInfo> list1 = new ArrayList<>();
        // 根据走货单统计出对应的数据
        List<FgShipmentInfo> list2 = new ArrayList<>();

        // 筛选船务确认走货信息
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLastComfirm() != null && (list.get(i).getLastComfirm().equals("船务") || list.get(i).getLastComfirm().equals("货仓"))) {

                list1.add(list.get(i));
                System.out.println(list.get(i).toString());
            }
        }
        // 找到与绑定走货区相等的走货单
        for (int i = 0;i < list1.size();i++) {
            if (fgShipmentInfo.getShipmentNO().toString().equals(list1.get(i).getShipmentNO().toString())) {
                list2.add(list1.get(i));
            }
        }
        // 对应日期没有走货单（日期变更）
        if (list2.size() == 0) {
            return  -10;
        }

        // 根据PN、PO统计数量是否改变
        int n = 0;
        long sumqty1 = 0l;
        long sumqty2 = 0l;
        boolean flag = true;
        for (int i = 0;i < list2.size();i++) {
            sumqty1 = sumQty(list2, list2.get(i).getShipmentNO().toString(), list2.get(i).getSapPn().toString(), list2.get(i).getPo().toString());
            sumqty2 = gMterialsMapper.getsumQty(list2.get(i));
            System.out.println("判断是否数量变更中" + sumqty1 + "==" + sumqty2);
            if (sumqty1 != sumqty2) {
                flag = false;
                n = -10;
                break;
            }
        }
        if (flag) {
            // 绑定走货区（是否需要再收货？）
            n = bindStockMapper.updateTosStock(toNo, stock);
        }
        return n;
    }

    @Override
    public String checkBHandQH(String uid) {

        String toNo = bindStockMapper.checkStatusByUid(uid) == null ? "" : bindStockMapper.checkStatusByUid(uid).toString();
        if ("".equals(toNo))
            return "UID对应备货单未完成拣货";
// 不会更新TOS备货单状态问题

//        FgShipmentInfo fgShipmentInfo = bindStockMapper.getShipmentInfoBytoNo(toNo);
//        if (saveInfoMapper.checkStatusTosQH(fgShipmentInfo.getShipmentNO().toString()) > 0)
//            return "走货单存在欠货单，不允许绑定走货区";
//
//        if (saveInfoMapper.checkStatusTosBH(fgShipmentInfo.getShipmentNO().toString()) > 0)
//            return "走货单未备完货，不允许绑定走货区";

        return toNo;
    }

    @Override
    public int checkStatusTosQH(String toNo) {
        return bindStockMapper.checkStatusTosQH(toNo);
    }

//    public String checkInstockAndBindstock(CheckList checkList, String stock, String username) {
//        String returnMessage = "";
//        // 已收货即313转数成功
//        System.out.println("sasasasas" + bindStockMapper.checkInstock(checkList));
//        int index = 0;
//        if (checkList.getPn().toString().substring(0,3).equals("650")) {
//            index = bindStockMapper.checkInstock2(checkList);
//        } else {
//            index = bindStockMapper.checkInstock(checkList);
//        }
//        if (index > 0) {
//            // 多个赋值可用BeanUtil中copyProperties方法优化
//            Inventory inventory = new Inventory();
//            System.out.println(checkList);
//            //a,b为对象
//            //BeanUtils.copyProperties(a, b);
//            //BeanUtils是org.springframework.beans.BeanUtils，a拷贝到b
//            //BeanUtils是org.apache.commons.beanutils.BeanUtils，b拷贝到a
//            BeanUtil.copyProperties(checkList, inventory, true);
//            // 上架的是650，把库存表partmunber设置为660，方便产生TO单
//            if (checkList.getPn().toString().substring(0, 3).equals("650")) {
//                inventory.setPn(checkList.getPn660().toString());
//                inventory.setPn650(checkList.getPn().toString());
//            }
//            System.out.println(checkList);
//            System.out.println(inventory);
//            inventory.setStock(stock);
//            inventory.setQa_sign(username);
//            List<Inventory> list1 = bindStockMapper.checkInfoByUid_1(inventory.getUid().toString());
//            if (list1.size() > 0) {
//                int n = bindStockMapper.updateStock(inventory);
//                if (n > 0)
//                    System.out.println("送检单绑库成功！");
//
//                returnMessage = "送检单移库成功";
//            } else {
//                int n = bindStockMapper.toinsert(inventory);
//                if (n > 0) {
//                    System.out.println("送检单绑库成功！");
//                    returnMessage = "送检单绑库成功";
//                } else {
//                    System.out.println("送检单绑库失败！");
//                    returnMessage = "送检单绑库失败！";
//                }
//            }
//        } else {
//            System.out.println("该成品单未收货");
//            returnMessage = "该成品单未收货";
//        }
//        return returnMessage;
//    }

    public String checkInstockAndBindstock(CheckList checkList, String stock, String username) {

        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = new ArrayList<>();
        String returnMessage = "";
        // 多个赋值可用BeanUtil中copyProperties方法优化
        Inventory inventory = new Inventory();
        System.out.println(checkList);
        //a,b为对象
        //BeanUtils.copyProperties(a, b);
        //BeanUtils是org.springframework.beans.BeanUtils，a拷贝到b
        //BeanUtils是org.apache.commons.beanutils.BeanUtils，b拷贝到a
        BeanUtil.copyProperties(checkList, inventory, true);
        // 已收货即313转数成功
        System.out.println("sasasasas" + bindStockMapper.checkInstock(checkList));
        int index = 0;
        if (checkList.getPn().toString().substring(0,3).equals("650")) {

            // 检查是否收货
            index = bindStockMapper.checkInstock2(checkList);
            // 上架的是650，把库存表partmunber设置为660，方便产生TO单
            inventory.setPn(checkList.getPn660().toString());
            inventory.setPn650(checkList.getPn().toString());
            list = sapUtil.getPoInfo(inventory.getPn());
            String s = "";
            // 获取上架PO对应的客户PO  （基于 上架一定上“收货人採購單號碼 ”PO，否则写法参考下面660）
            for (String[] arr : list) {
                if (Arrays.asList(arr).contains(checkList.getPo().toString())) {
                    s = arr[1];
                    break;
                }
            }
            System.out.println("65000" + s);
            // 设置客户PO  （该PO为客户采购单号）
            inventory.setClientPO(s);

        } else {

            /**  目前只有650/650对应的660 输入的PO是采购人订单号（SAP右边的），其余正常的660都是客户订单号（SAP左边）  */
            index = bindStockMapper.checkInstock(checkList);
            list = sapUtil.getPoInfo(inventory.getPn());
            String s = "";
            // 获取上架PO对应的采购人PO
            for (String[] arr : list) {
                if (Arrays.asList(arr).contains(checkList.getPo().toString())) {
                    if (arr[2] != null && !"".equals(arr[2])) {
                        // 如果上架是“收货人採購單號碼”则赋值“客戶採購單號碼 ”给clientPO，反之 一样
                        if (arr[2].equals(inventory.getPo())) {
                            s = arr[1];
                        } else {
                            s = arr[2];
                        }
                    }
                    break;
                }
            }
            System.out.println("65000" + s);
            // 设置客户PO （该PO为收货人采购单号）
            inventory.setClientPO(s);
        }
        if (index > 0) {

            System.out.println(checkList);
            System.out.println(inventory);
            inventory.setStock(stock);
            inventory.setQa_sign(username);
            List<Inventory> list1 = bindStockMapper.checkInfoByUid_1(inventory.getUid().toString());
            if (list1.size() > 0) {
                int n = bindStockMapper.updateStock(inventory);
                if (n > 0)
                    System.out.println("送检单绑库成功！");

                returnMessage = "送检单移库成功";
            } else {
                int n = bindStockMapper.toinsert(inventory);
                if (n > 0) {
                    System.out.println("送检单绑库成功！");
                    returnMessage = "送检单绑库成功";
                } else {
                    System.out.println("送检单绑库失败！");
                    returnMessage = "送检单绑库失败！";
                }
            }
        } else {
            System.out.println("该成品单未收货");
            returnMessage = "该成品单未收货";
        }
        return returnMessage;
    }


    /**
     * 统计数量
     * */
    public long sumQty(List<FgShipmentInfo> fgShipmentInfos, String shipmentNo, String pn, String  po) {

        long sumqty = 0l;
        for (int i = 0; i < fgShipmentInfos.size(); i++) {
            if (fgShipmentInfos.get(i).getShipmentNO().toString().equals(shipmentNo) && fgShipmentInfos.get(i).getSapPn().toString().equals(pn) &&
                    fgShipmentInfos.get(i).getPo().toString().equals(po)) {
                sumqty += fgShipmentInfos.get(i).getQuantity();

            }
        }
        return sumqty;

    }
}
