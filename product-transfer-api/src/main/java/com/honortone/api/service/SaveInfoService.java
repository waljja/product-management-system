package com.honortone.api.service;

import com.honortone.api.controller.dto.SaveInfoDto;
import com.honortone.api.controller.dto.ShipmentAndTos;
import com.honortone.api.entity.SealNoInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface SaveInfoService {

//    List<String> downloadShipmentNo();

    List<String> downloadShipmentNo2(String date);

    ShipmentAndTos getinfoByshipmentNo(String shipmentNo);

    int checkSealNo(String sealNo);

    int checkSealNoAndCar(@Param("sealNo") String sealNo, @Param("carNo") String carNo);

    int checkTosStatus(String shipmentNo);

    public String updateInfo(SealNoInfo sealNoInfo, MultipartFile upload);
//    public String saveInfo(SealNoInfo sealNoInfo);

    public ArrayList<SealNoInfo> findInfo(String ht_seal, String shipmentNo);
}
