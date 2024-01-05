package com.example.productkanbanapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 出货计划看板实体类
 *
 * @author 丁国钊
 * @date 2023-08-21-9:53
 */
@Data
public class Shipment {

    /**
     * 出货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date shipmentDate;

    /**
     * 装车时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date loadingTime;

    /**
     * 走货车型
     */
    private String shipmentCar;

    /**
     * 车牌号码
     */
    private String licenseNumber;

    /**
     * 出货单号
     */
    private String shipmentNo;

    /**
     * TO单号
     */
    private String toNo;

    /**
     * 客户编号
     */
    private String clientCode;

    /**
     * 状态
     */
    private String state;

    /**
     * TO数量
     */
    private Float toQty;

    /**
     * 出货数量
     */
    private Double shipmentQty;

    /**
     * 装箱数量
     */
    private Double boxQty;

    /**
     * 卡板数量
     */
    private Double palletQty;

    /**
     * 零部件号
     */
    private List<String> partNumberList;

}
