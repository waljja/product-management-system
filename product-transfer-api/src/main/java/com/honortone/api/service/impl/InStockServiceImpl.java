package com.honortone.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honortone.api.common.Constants;
import com.honortone.api.controller.dto.IUserDto;
import com.honortone.api.controller.dto.MaterialTransationsDto;
import com.honortone.api.entity.CheckList;
import com.honortone.api.entity.IUser;
import com.honortone.api.entity.MaterialTransactions;
import com.honortone.api.exception.ServiceException;
import com.honortone.api.mapper.InStockMapper;
import com.honortone.api.service.InStockServicr;
import com.honortone.api.entity.Inventory;
import com.honortone.api.utils.SAPUtil;
import com.honortone.api.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 收货接口实现类
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-13
 */

@Service
public class InStockServiceImpl extends ServiceImpl<InStockMapper, Inventory> implements InStockServicr {

    @Autowired
    private InStockMapper inStockMapper;


    @Override
    public MaterialTransationsDto ifSuccess(String uid, String stock) {

        SAPUtil sapUtil = new SAPUtil();
        MaterialTransationsDto materialTransationsDto = new MaterialTransationsDto();
        IUserDto iUserDto = new IUserDto();
        // 用户是否登录
        //GetSession.getUsernameFromSession(session);
        //session.getAttribute("username");
        // 根据扫描UID查询批次、物料、工厂
        CheckList checkList = inStockMapper.ifSuccess(uid);
        if (checkList == null) {
            materialTransationsDto.setMsg("未找到该UID信息，请检查是否输入错误!");
            return materialTransationsDto;
        }
        System.out.println(checkList);
        // System.out.println("qa:" + checkList.getQa_result());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        System.out.println(checkList.getProduction_date());
        String productDate = sdf.format(checkList.getProduction_date());
        System.out.println("日期：" + productDate);
        List<String[]> list = sapUtil.Z_HTMES_YMMR04(checkList.getPlant().toString(), checkList.getPn().toString(), productDate, productDate);
        System.out.println("查询QA结果数量：" + list.size());
        if (list.size() == 0) {
            materialTransationsDto.setMsg("未查询到QA检验结果，不允许收货");
        } else {
            int n1 = 0;
            for (String[] arr : list) {
                System.out.println(Arrays.asList(arr[0]));
                if (Arrays.asList(arr[0]).contains("321")) {
                    int n = inStockMapper.updateQAresult(checkList.getPn().toString(), 1, checkList.getPlant().toString(), checkList.getBatch().toString());
                    materialTransationsDto = insertInfo(checkList, arr[1].toString(), stock);
                    break;
                }
                n1 += 1;
                if (n1 == list.size()) {
                    int n = inStockMapper.updateQAresult(checkList.getPn().toString(), 0, checkList.getPlant().toString(), checkList.getBatch().toString());
                    materialTransationsDto.setMsg("查询结果为fail，不允许收货");
                }
                // 直接输出是带有[]框
//                String batch = Arrays.asList(arr[3]).toString();
//                String Mvt = Arrays.asList(arr[0]).toString();
//                if (batch.substring(1, batch.length() -1).equals(checkList.getBatch().toString())) {
//                    if (Mvt.substring(1, Mvt.length() - 1).equals("321")) {
//                        int n = inStockMapper.updateQAresult(checkList.getPn().toString(), 1, checkList.getPlant().toString(), checkList.getBatch().toString());
//                        materialTransationsDto = insertInfo(checkList, arr[1].toString(), stock);
//                        break;
//                    } else if (Mvt.substring(1, Mvt.length() - 1).equals("122")) {
//                        int n = inStockMapper.updateQAresult(checkList.getPn().toString(), 0, checkList.getPlant().toString(), checkList.getBatch().toString());
//                        materialTransationsDto = insertInfo(checkList, arr[1].toString(), stock);
//                        materialTransationsDto.setMsg("查询结果为fail，不允许收货");
//                        break;
//                    }
//                }
//                n1 += 1;
//                if (n1 == list.size()) {
////                    int n = inStockMapper.updateQAresult(checkList.getPn().toString(), 0, checkList.getPlant().toString(), checkList.getBatch().toString());
//                    materialTransationsDto.setMsg("QA未检验，不允许收货");
//                }
//            }
            }
        }
        System.out.println("2" + materialTransationsDto.getMsg().toString());
        return materialTransationsDto;
    }


    @Override
    public String InStock(MaterialTransationsDto materialTransationsDto) {
        String returnMessage = "";
        // 根据物料PN、批次、工厂、315查询统计收货总数
        System.out.println(materialTransationsDto);
        float quantity_sum = inStockMapper.selectQuantity(materialTransationsDto);
        // 收货总数与SAP中313批次总数对比
        if (materialTransationsDto.getQuantity() == quantity_sum) {
            int n = inStockMapper.updateRecordStatus(materialTransationsDto);
            if (n > 0) {
                returnMessage = "收货成功！";
            } else {
                returnMessage = "提交失败,请联系开发人员！";
            }
        } else if (materialTransationsDto.getQuantity() < quantity_sum) {
            System.out.println("收货总数大于批次总数，请检查是否拿错成品单");
            returnMessage = "收货总数大于批次总数，请检查是否拿错成品单";
        } else {
            System.out.println("还有UID未收货！");
            returnMessage = "还有UID未收货！";
        }

        return returnMessage;
    }

    public MaterialTransationsDto insertInfo(CheckList checkList, String from_stock, String to_stock) {
        SAPUtil sapUtil = new SAPUtil();
        MaterialTransationsDto materialTransationsDto = new MaterialTransationsDto();
        IUserDto iUserDto = new IUserDto();
        // 多个赋值可用BeanUtil中copyProperties方法优化
        String b = checkList.getBatch();
        String p = checkList.getPn();
        String pl = String.valueOf(checkList.getPlant());
        List<String[]> list = sapUtil.getInfo(pl, p, b, "313");
        String[] arr = null;
        int sum_qty = 0;
        System.out.println(list.size());
        String RefDocNo = "";
        String DocHeader = "";
        // 313对应数据一般是两条，一条负(313转出），一条正（转入315）
        if (list.size() > 0) {

            arr = list.get(0);
            sum_qty = arr[7].substring(0, 1).equals("-") ? Integer.parseInt(arr[7].substring(1, arr[7].length())) : Integer.parseInt(arr[7]);
            // 313库位
            from_stock = arr[2].toString();
            // 313转315库位
            to_stock = list.get(1)[2].toString();
//            if (iUserDto.getUsername() == null || iUserDto.getUsername().equals("")) {
//                throw new ServiceException(Constants.CODE_401, "请登录账号！");
//            } else {
            // 是否存在数据（mysql不能使用if not exists）
            //List<MaterialTransactions> list1 = inStockMapper.checkInfoByUid(uid);
            System.out.println(checkList.getUid().toString());

            // 显示物料PN、UID数量待货仓人员确认
            materialTransationsDto.setPn(checkList.getPn());
            materialTransationsDto.setBatch(checkList.getBatch());
            materialTransationsDto.setPlant(checkList.getPlant());
            materialTransationsDto.setTransactiontype("315");
            materialTransationsDto.setQuantity(checkList.getUid_no());
            materialTransationsDto.setFromstock(from_stock);
            materialTransationsDto.setTostock(to_stock);
            materialTransationsDto.setSap101(checkList.getSap101());
            System.out.println("-=-=" + checkList.getPn().toString().substring(0, 3));

            if (checkList.getPn().toString().substring(0, 3).equals("650")) {
                // 查询是否已过账，避免影响统计总数
                int n = inStockMapper.checkInfoAlreadyTransfer2(materialTransationsDto);
                if (n > 0) {
                    materialTransationsDto.setMsg("该过账编号已过账");
                } else {
                    // 同PN、批次、工厂、315已收货的总数
                    float quantity_sum = inStockMapper.selectQuantity2(materialTransationsDto);
                    // 统计新收货的UID数和已收货总数和  是否大于批量，大于则不允许收货
                    float quantity_sum2 = quantity_sum + checkList.getUid_no();
                    int result = Float.compare(quantity_sum2, checkList.getBatch_qty());
                    System.out.println("jie" + result + "===" + quantity_sum2 + "---" + checkList.getBatch_qty());
                    // quantity_sum2 <= checkList.getBatch_qty()
                    if (result <= 0) {
                        //int n = inStockMapper.toInsert(checkList, checkList.getQa_sign() == null ? "" : checkList.getQa_sign().toString(), from_stock, to_stock);
                        materialTransationsDto.setMsg("允许收货");
                    } else {
                        materialTransationsDto.setMsg("收货后总数大于批量，不允许该UID收货");
                    }
                }
            } else {
                // 查询是否已过账，避免影响统计总数
                int n = inStockMapper.checkInfoAlreadyTransfer(materialTransationsDto);
                if (n > 0) {
                    materialTransationsDto.setMsg("该批次已过账");
                } else {
                    // 同PN、批次、工厂、315已收货的总数
                    float quantity_sum = inStockMapper.selectQuantity(materialTransationsDto);
                    // 统计新收货的UID数和已收货总数和  是否大于批量，大于则不允许收货
                    float quantity_sum2 = quantity_sum + checkList.getUid_no();
                    int result = Float.compare(quantity_sum2, checkList.getBatch_qty());
                    System.out.println("jie" + result + "===" + quantity_sum2 + "---" + checkList.getBatch_qty());
                    // quantity_sum2 <= checkList.getBatch_qty()
                    if (result <= 0) {
                        //int n = inStockMapper.toInsert(checkList, checkList.getQa_sign() == null ? "" : checkList.getQa_sign().toString(), from_stock, to_stock);
                        materialTransationsDto.setMsg("允许收货");
                    } else {
                        materialTransationsDto.setMsg("收货后总数大于批量，不允许该UID收货");
                    }
                }
            }

            // }
        } else {
//            int n = inStockMapper.toInsert(checkList, checkList.getQa_sign() == null ? "" : checkList.getQa_sign().toString(), from_stock, to_stock);
//            // 将物料PN、批次、工厂、SAP查到的313和批次总数封装传到前端
//            materialTransationsDto.setPn(checkList.getPn());
//            materialTransationsDto.setBatch(checkList.getBatch());
//            materialTransationsDto.setPlant(checkList.getPlant());
//            materialTransationsDto.setTransactiontype("315");
//            materialTransationsDto.setQuantity(200);
//            if (n > 0) {
//                materialTransationsDto.setMsg("数据插入成功");
//            } else {
//                materialTransationsDto.setMsg("已存在该数据");
//            }
            materialTransationsDto.setMsg("该成品单未执行313转数");
        }
        System.out.println("1" + materialTransationsDto.getMsg().toString());
        return materialTransationsDto;
    }

    @Override
    public String inAndUpdateStatus(String uid, String fromstock, String tostock, String username) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SAPUtil sapUtil = new SAPUtil();
        String returnMessage = "";
        MaterialTransationsDto materialTransationsDto = new MaterialTransationsDto();
        CheckList checkList = inStockMapper.ifSuccess(uid);
        String b = checkList.getBatch();
        String p = checkList.getPn();
        String pl = String.valueOf(checkList.getPlant());
        String RefDocNo = "";
        String DocHeader = "";
        /** 650的PN（即无批次），直接收货，不需要判SAP是否313转数，过账表中RefDocNo字段存放101过账编号，当该过账编号对应数量累加等于该过账编号的批量
         *  即提交自动过账315（此时程序会自动判该成品是否有313转数
         *  有则成功，无则失败【HT-MES 2.0 可查看】，转入转出库位分别为：DJ80\RH82），原理同有批次类似，跟有批次相比 少了调用SAP接口查313是否转数
        */
        if (p.substring(0, 3).equals("650")) {

            int n1 = inStockMapper.toInsert(checkList, username, "DJ80", "RH82", checkList.getSap101(), sdf.format(checkList.getProduction_date()).substring(0, 4));
            if (n1 > 0) {
                returnMessage = "收货成功";
            } else {
                return "该UID已收货";
            }
            materialTransationsDto.setPn(checkList.getPn());
            materialTransationsDto.setBatch(checkList.getBatch());
            materialTransationsDto.setPlant(checkList.getPlant());
            materialTransationsDto.setSap101(checkList.getSap101());
            materialTransationsDto.setTransactiontype("315");
            // 收货后  总数是否等于批量   是则自动过账
            float quantity_sum = inStockMapper.selectQuantity2(materialTransationsDto);
            int result = Float.compare(quantity_sum, checkList.getBatch_qty());
            // quantity_sum == checkList.getBatch_qty()
            if (result == 0) {
                int n2 = inStockMapper.updateRecordStatus2(materialTransationsDto);
                if (n2 > 0) {
                    returnMessage = "收货成功，已自动过账315";
                } else {
                    returnMessage = "收货成功，315过账失败";
                }
            }
        } else {
            List<String[]> list = sapUtil.getInfo(pl, p, b, "313");
            if (list.size() > 0) {
                // 313过账编号、时间（年份）
                RefDocNo = list.get(0)[4].toString();
                DocHeader = list.get(0)[5].toString().substring(0, 4);

                int n = inStockMapper.toInsert(checkList, username, fromstock, tostock, RefDocNo, DocHeader);
                if (n > 0) {
                    returnMessage = "收货成功";
                } else {
                    return "该UID已收货";
                }
                materialTransationsDto.setPn(checkList.getPn());
                materialTransationsDto.setBatch(checkList.getBatch());
                materialTransationsDto.setPlant(checkList.getPlant());
                materialTransationsDto.setTransactiontype("315");
                // 收货后  总数是否等于批量   是则自动过账
                float quantity_sum = inStockMapper.selectQuantity(materialTransationsDto);
                int result = Float.compare(quantity_sum, checkList.getBatch_qty());
                // quantity_sum == checkList.getBatch_qty()
                if (result == 0) {
                    int n1 = inStockMapper.updateRecordStatus(materialTransationsDto);
                    if (n1 > 0) {
                        returnMessage = "收货成功，已自动过账315";
                    } else {
                        returnMessage = "收货成功，315过账失败";
                    }
                }

            } else {
                returnMessage = "该PN未313转数";
            }
        }
        return returnMessage;
    }


}
