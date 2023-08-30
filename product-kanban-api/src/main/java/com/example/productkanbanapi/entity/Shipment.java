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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date shipmentDate;

    /**
     * 出货单号
     */
    private String shipmentNo;

    /**
     * 客户编号
     */
    private String clientCode;

    /**
     * 订单号
     */
    private String po;

    /**
     * 状态
     */
    private String state;

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
