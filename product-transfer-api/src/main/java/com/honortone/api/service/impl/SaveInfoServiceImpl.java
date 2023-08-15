package com.honortone.api.service.impl;

import com.honortone.api.common.Result;
import com.honortone.api.controller.dto.SaveInfoDto;
import com.honortone.api.controller.dto.ShipmentAndTos;
import com.honortone.api.entity.FgShipmentInfo;
import com.honortone.api.entity.SealNoInfo;
import com.honortone.api.entity.ToList;
import com.honortone.api.mapper.BindStockMapper;
import com.honortone.api.mapper.GMterialsMapper;
import com.honortone.api.mapper.SaveInfoMapper;
import com.honortone.api.service.SaveInfoService;
import com.honortone.api.utils.SAPUtil;
import com.honortone.api.utils.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaveInfoServiceImpl implements SaveInfoService {

    @Autowired
    private SaveInfoMapper saveInfoMapper;

    @Autowired
    private BindStockMapper bindStockMapper;

    @Autowired
    private GMterialsMapper gMterialsMapper;

//    @Override
//    public List<String> downloadShipmentNo() {
//
//        List<String> list = saveInfoMapper.downloadShipmentNo();
//        return list;
//    }

    @Override
    public List<String> downloadShipmentNo2(String date) {
        List<String> list = saveInfoMapper.downloadShipmentNo2(date);
        return list;
    }

    @Override
    public ShipmentAndTos getinfoByshipmentNo(String shipmentNo) {

        ShipmentAndTos shipmentAndTos = new ShipmentAndTos();
        // 欠货单是否已补货
        if (saveInfoMapper.checkStatusTosQH(shipmentNo) > 0) {
            shipmentAndTos.setShipmentNo("欠货单还未补货");
            return shipmentAndTos;
        }
        // 备货单是否已拣货完成
        if (saveInfoMapper.checkStatusTosBH(shipmentNo) > 0) {
            shipmentAndTos.setShipmentNo("存在未拣货备货单/未做回仓");
            return shipmentAndTos;
        }
        shipmentAndTos = saveInfoMapper.getinfoByshipmentNo(shipmentNo);

        // Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDate = simpleDateFormat.format(shipmentAndTos.getShipmentDate());

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
        // 找到与保存走货信息相等的走货单
        for (int i = 0;i < list1.size();i++) {
            if (shipmentNo.equals(list1.get(i).getShipmentNO().toString())) {
                list2.add(list1.get(i));
            }
        }
        // 对应日期没有走货单（日期变更）
        if (list2.size() == 0) {
            shipmentAndTos.setShipmentNo("走货日期变更");
            return shipmentAndTos;
        }

        // 根据PN、PO统计数量是否改变
        long sumqty1 = 0l;
        long sumqty2 = 0l;
        for (int i = 0;i < list2.size();i++) {
            sumqty1 = sumQty(list2, list2.get(i).getShipmentNO().toString(), list2.get(i).getSapPn().toString(), list2.get(i).getPo().toString());
            sumqty2 = gMterialsMapper.getsumQty(list2.get(i));
            System.out.println("判断是否数量变更中" + sumqty1 + "==" + sumqty2);
            if (sumqty1 != sumqty2) {
                shipmentAndTos.setShipmentNo("走货数量变更");
                break;
            }
        }
        return shipmentAndTos;
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

    @Override
    public int checkSealNo(String sealNo) {
        return saveInfoMapper.checkSealNo(sealNo);
    }

    @Override
    public int checkSealNoAndCar(String sealNo, String carNo) {
        return saveInfoMapper.checkSealNoAndCar(sealNo, carNo);
    }

    @Override
    public int checkTosStatus(String shipmentNo) {
        return saveInfoMapper.checkTosStatus(shipmentNo);
    }

    @Override
    public String updateInfo(SealNoInfo sealNoInfo, MultipartFile upload) {

        String returnMessage = "";
        // 写入临时文件
        //MultipartFile file = FileUtils.getMultipartFile((File) upload);
        String fileName = null;
        if (!sealNoInfo.getAutoTransfer().toString().equals("311")) {
//            if (saveInfoMapper.checkSealNo2(sealNoInfo) > 0)
//                return "该封条号已被使用！";

            // 不需要自动过账
            returnMessage = saveInfo(sealNoInfo, upload);
            return returnMessage;

        } else {
            List<ToList> toLists = saveInfoMapper.autoTransfer(sealNoInfo.getShipmentNo().toString());
            if (toLists.size() <= 0)
                return returnMessage = "备货单找不到相关信息";

            if (saveInfoMapper.checkSealNo2(sealNoInfo) > 0)
                return "该封条号已被使用！";

            String yORn = "";
            for (int i = 0; i < toLists.size(); i++) {
                int n = saveInfoMapper.toInsert(toLists.get(i));
                System.out.println(n + "3333");
                if (n <= 0) {
                    System.out.println("过账失败UID：" + toLists.get(i).getUid().toString());
                    yORn = yORn + toLists.get(i).getUid().toString() + "、";
                    returnMessage = "UID:" + yORn + "、过账失败，请查看后台输出";
                }
            }
            if (returnMessage.contains("过账失败"))
                return returnMessage;

            returnMessage = saveInfo(sealNoInfo, upload);
            if ("上传成功".equals(returnMessage)) {
                returnMessage = "自动过账311成功，并已保存走货信息";
            } else {
                returnMessage = "自动过账311成功，走货信息保存上失败";
            }
        }
        return returnMessage;

    }

//    @Override
//    public String saveInfo(SealNoInfo sealNoInfo) {
//
//        String re = "";
//        int n = saveInfoMapper.saveInfo(sealNoInfo);
//        if(n > 0){
//            re = "保存成功";
//        }else {
//            re = "保存失败";
//        }
//        return re;
//    }

    @Override
    public ArrayList<SealNoInfo> findInfo(String ht_seal, String shipmentNo) {

        ArrayList<SealNoInfo> sealNoInfo = new ArrayList<SealNoInfo>();
        sealNoInfo = saveInfoMapper.findInfo(ht_seal,shipmentNo);
        return sealNoInfo;
    }

    public String saveInfo(SealNoInfo sealNoInfo, MultipartFile upload) {
        String returnMessage = "";
        String fileName = "";
        try {
            // 获取上传后的文件名
            fileName = FileUploadUtils.uploadMinio(upload);
            System.out.println(fileName);
            if (fileName != null && fileName != "") {
                sealNoInfo.setFilepath(fileName);
                sealNoInfo.setFilename(fileName.substring(43).toString());
                System.out.println("jjttmm:" + fileName);
                int n = saveInfoMapper.insertSealnoInfo(sealNoInfo);
                if (n > 0) {
                    returnMessage = "上传成功";
                } else {
                    returnMessage = "上传保存失败";
                }
//                if (saveInfoMapper.checkSealNo2(sealNoInfo) > 0) {
//                    System.out.println("第二张照片");
//                    saveInfoMapper.insertSealnoInfo(sealNoInfo);
//                } else {
//                    System.out.println("第一张照片");
//                    int n = saveInfoMapper.updateInfo(sealNoInfo);
//                    if (n > 0) {
//                        returnMessage = "上传成功";
//                    } else {
//                        returnMessage = "上传保存失败";
//                    }
//                }
            } else {
                returnMessage = "上传失败，上传图片文件名获取失败";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMessage;
    }
}
