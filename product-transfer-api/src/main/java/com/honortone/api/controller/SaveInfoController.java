package com.honortone.api.controller;


import com.honortone.api.common.Result;
import com.honortone.api.controller.dto.SaveInfoDto;
import com.honortone.api.controller.dto.ShipmentAndTos;
import com.honortone.api.entity.SealNoInfo;
import com.honortone.api.entity.ToList;
import com.honortone.api.service.SaveInfoService;
import com.honortone.api.utils.GetSession;
import com.honortone.api.utils.file.FileUploadUtils;
import com.honortone.api.utils.file.FileUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 保存走货信息接口
 * </p>
 *
 * @author 江庭明
 * @since 2023-03-10
 */

@Slf4j
@Controller
@Api("保存走货信息接口")
@RequestMapping(value = "/saving")
public class SaveInfoController {

    @Autowired
    private SaveInfoService saveInfoService;

//    /** 下载走货单 */
//    @ResponseBody
//    @PostMapping(value = "/downloadOrder")
//    public Result downloadOrder(){
//
//        List<String> list = saveInfoService.downloadShipmentNo();
//        if (list.size() > 0) {
//            return Result.success(list);
//        } else {
//            return Result.error("600","未查询到走货单，请联系开发人员");
//        }
//    }

    @ResponseBody
    @PostMapping(value = "/downloadShipmentNo")
    public Result downloadShipmentNo(@RequestBody String date){

        List<String> list = saveInfoService.downloadShipmentNo2(date);
        if (list.size() > 0) {
            return Result.success(list);
        } else {
            return Result.error("600","未查询到走货单，请联系开发人员");
        }
    }

    /** 判断是否可以保存 */
    @ResponseBody
    @PostMapping(value = "/getinfobyshipmentno")
    public Result getinfoByshipmentNo(@RequestBody String shipmentNo){

        System.out.println(shipmentNo);
        ShipmentAndTos shipmentAndTos = saveInfoService.getinfoByshipmentNo(shipmentNo);
        if (shipmentAndTos.getShipmentNo() != null && shipmentAndTos.getShipmentNo().toString().equals("欠货单还未补货")) {
            return Result.error("600","欠货单还未补货");
        } else if (shipmentAndTos.getShipmentNo() != null && shipmentAndTos.getShipmentNo().toString().equals("存在未拣货备货单/未做回仓")) {
            return Result.error("600","存在未拣货备货单/未做回仓");
        } else if (shipmentAndTos.getShipmentNo() != null && shipmentAndTos.getShipmentNo().toString().equals("走货日期变更")) {
            return Result.error("600", "走货日期变更");
        } else if (shipmentAndTos.getShipmentNo() != null && shipmentAndTos.getShipmentNo().toString().equals("走货数量变更")) {
            return  Result.error("600", "走货数量变更");
        } else {
            return Result.success(shipmentAndTos);
        }
    }

//    保存到本地
//    @ResponseBody
//    @PostMapping(value ="/saveInfo")
//    public Result saveInfo(@RequestParam("newFile") MultipartFile upload,
//                           SaveInfoDto saveInfoDto) throws IOException {
//
//        // 前端multipart/form-data参数不能使用@RequestBody获取，不写即可
//
//        String returnMessage = "";
//
//        System.out.println("走货单："+ saveInfoDto.getBill() +"--车号："+saveInfoDto.getCarno()+"--货柜："+saveInfoDto.getContainer()+"--型号："+saveInfoDto.getModelno()+
//                "--鸿通封条："+saveInfoDto.getHt_seal()+"--货柜封条："+saveInfoDto.getHg_seal());
//
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time = sdf.format(date);
//        System.out.println("上传日期："+time);
//
//       /* Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH + 1);
//        int day = calendar.get(Calendar.DATE);
//        int hour = calendar.get(Calendar.HOUR);
//        int minute = calendar.get(Calendar.MINUTE);
//        int second = calendar.get(Calendar.SECOND);
//        System.out.println("上传日期："+year+"-"+month+"-"+day+" "+hour+"-"+minute+"-"+second);*/
//
//        GetSession getSession = new GetSession();
//        // 设置文件存放路径
//        //String path = ResourceUtils.getURL("classpath:").getPath() + "static/upload/";
//        String path = "/D:/Hb/project/FGManagementPDA/static/uploadImages/";
//        // 该path前有一个斜杠/
//        System.out.println("文件存放路径："+path);
//        // 文件上传成功返回新的文件名（或路径+文件名）
//        String filename = getSession.UploadFile(upload,path);
//        if(!filename.equals("上传失败")){
//            // 去掉/
//            String path1 = path.substring(1,path.length());
//            System.out.println("去掉/--"+path1);
//
//            DecimalFormat df = new DecimalFormat("0.00");
//            float s = 0;
//            FileInputStream fis =new FileInputStream(path1 + filename);
//            s = fis.available();
//            String ss = df.format(s/1024/1024);
//            System.out.println("文件大小:"+s+"(bytes) || "+ ss +"(Mb)");
//
//            saveInfoDto.setPath(path1);
//            saveInfoDto.setFilename(filename);
//            System.out.println(saveInfoDto.getPath() + "===" + saveInfoDto.getFilename());
//            //String filename = upload.getOriginalFilename();
//            //String fileUuid = UUID.randomUUID().toString().replace("-", "");
//
//            String returnMessage1 = saveInfoService.saveInfo(saveInfoDto);
//            if(returnMessage1.equals("保存成功")){
//                returnMessage = "上传成功";
//            }else {
//                returnMessage = "上传失败";
//            }
//        }else {
//            returnMessage = "上传失败";
//        }
//        return Result.success(returnMessage);
//    }

    /**
     * 保存到MINIO
     * */
    @ResponseBody
    @PostMapping(value ="/saveInfo")
    public Result saveInfo(@RequestParam("newFile") MultipartFile upload,
                           SealNoInfo sealNoInfo, HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession();
        System.out.println("j" + session.getAttribute("username"));

        System.out.println("测试");

        if (saveInfoService.checkSealNo(sealNoInfo.getSealNo().toString()) <= 0)
            return Result.error("600", "不存在该鸿通封条号！或该封条已被使用");

        if (saveInfoService.checkSealNoAndCar(sealNoInfo.getSealNo().toString(), sealNoInfo.getCarNo().toString()) > 0)
            return Result.error("600", "该封条已被其它车号使用");

        if (saveInfoService.checkTosStatus(sealNoInfo.getShipmentNo().toString()) > 0)
            return Result.error("600", "变更的走货清单未做回仓操作");

        String returnMessage = saveInfoService.updateInfo(sealNoInfo, upload);
        if ("上传成功".equals(returnMessage) || "".equals(returnMessage)) {
            returnMessage = "上传成功";
            return Result.success(returnMessage);
        } else if (returnMessage.contains("自动过账311成功")) {
            return Result.success(returnMessage);
        } else {
            return Result.error("600", returnMessage);
        }
        // 写入临时文件
        //MultipartFile file = FileUtils.getMultipartFile((File) upload);
//        String fileName = null;
//        if (!sealNoInfo.getAutoTransfer().toString().equals("311")) {
//
//            try {
//                // 获取上传后的文件名
//                fileName = FileUploadUtils.uploadMinio(upload);
//                System.out.println(fileName);
//                if (fileName != null && fileName != "") {
//                    sealNoInfo.setFilepath(fileName);
//                    sealNoInfo.setFilename(fileName.substring(fileName.length() - 31, fileName.length()).toString());
//                    returnMessage = saveInfoService.updateInfo(sealNoInfo);
//                    if (returnMessage.equals("保存成功")) {
//                        returnMessage = "上传成功";
//                        return Result.success(returnMessage);
//                    } else {
//                        returnMessage = "上传保存失败";
//                        return Result.error("600", returnMessage);
//                    }
//                } else {
//                    returnMessage = "上传失败，上传图片文件名获取失败";
//                    return Result.error("600", returnMessage);
//                }
//            } catch (IOException e) {
//                System.out.println("测试2");
//                e.printStackTrace();
//                return Result.error("600", "发生异常");
//            } finally {
//                // 删除掉临时文件
////            if (upload != null && upload.exists()) {
////                FileUtils.deleteFile(upload.getAbsolutePath());
////            }
//            }
//        } else {
//
//            return Result.success(returnMessage);
//        }

    }
// 只能保存/查看一张图片 问题
    /**
     * 查询保存的走货信息
     */
    @ResponseBody
    @PostMapping(value = "/findInfo")
    public Result findInfo(SaveInfoDto saveInfoDto) {
        String returnMessage = "";
        System.out.println(saveInfoDto.getHt_seal() + saveInfoDto.getHg_seal() + saveInfoDto.getShipmentNo().toString());
        ArrayList<SealNoInfo> list = saveInfoService.findInfo(saveInfoDto.getHt_seal(), saveInfoDto.getShipmentNo());
        if (list.size() > 0) {
            for (SealNoInfo sealNoInfo : list) {
                System.out.println(sealNoInfo.getFilepath() + "----" + sealNoInfo.getFilename());
            }
            return Result.success(list);
        } else {
            System.out.println("查询未果");
            return Result.error("400", "没有该条数据");
        }
        //SealNoInfo sealNoInfo = saveInfoService.findInfo(saveInfoDto.getHt_seal(),saveInfoDto.getHg_seal());
        //System.out.println(sealNoInfo.getPath() + "----" + sealNoInfo.getFilename());

    }
}
