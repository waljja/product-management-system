package com.honortone.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *     封装调用SAP接口获得的数据
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */
@Data
public class SapReturn {
    @ApiModelProperty("工厂")
    private String werks;

    @ApiModelProperty("物料")
    private String matnr;

    @ApiModelProperty("存储地点")
    private String logrt;

    @ApiModelProperty("存货管理")
    private String bwart;

    @ApiModelProperty("物料文件")
    private String mblnr;

    @ApiModelProperty("过账日期")
    private String budat;

    @ApiModelProperty("输入时间")
    private String cputm;

    @ApiModelProperty("数量")
    private String menge;

    @ApiModelProperty("基础计量单位")
    private String meins;

    @ApiModelProperty("批次")
    private String charg;

    @ApiModelProperty("订单")
    private String aufnr;

    @ApiModelProperty("客户账号")
    private String kunnr;

}
