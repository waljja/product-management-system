package com.honortone.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.honortone.api.controller.dto.SaveInfoDto;
import com.honortone.api.controller.dto.ShipmentAndTos;
import com.honortone.api.entity.SealNoInfo;
import com.honortone.api.entity.ToList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

@Mapper
@Repository
public interface SaveInfoMapper {

//    @DS("slave_2")
//    List<String> downloadShipmentNo();

    @DS("slave_2")
    List<String> downloadShipmentNo2(String date);

    @DS("slave_2")
    int checkStatusTosQH(String shipmentNo);

    @DS("slave_2")
    int checkStatusTosBH(String shipmentNo);

    @DS("slave_2")
    ShipmentAndTos getinfoByshipmentNo(String shipmentNo);

    @DS("slave_2")
    List<ToList> autoTransfer(String shipmentNo);

    int toInsert(ToList toList);

    @DS("slave_2")
    int checkSealNo(String sealNo);

    @DS("slave_2")
    int checkSealNoAndCar(@Param("sealNo") String sealNo, @Param("carNo") String carNo);

    @DS("slave_2")
    int checkTosStatus(String shipmentNo);

    @DS("slave_2")
    int updateInfo(SealNoInfo sealNoInfo);
//    @DS("slave_2")
//    int saveInfo(SealNoInfo sealNoInfo);
    @DS("slave_2")
    int checkSealNo2(SealNoInfo sealNoInfo);

    @DS("slave_2")
    int insertSealnoInfo(SealNoInfo sealNoInfo);

    @DS("slave_2")
    ArrayList<SealNoInfo> findInfo(@Param("sealNo") String sealNo, @Param("shipmentNo") String shipmentNo);
}
