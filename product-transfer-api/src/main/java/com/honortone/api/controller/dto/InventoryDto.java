package com.honortone.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 接收前端请求参数（字段名与前端参数字段名一致）
 */
@Data
public class InventoryDto {

    @ApiModelProperty(value = "型号")
    private String pn;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "成品单号")
    private String uid;

    @ApiModelProperty(value = "数量")
    private float uid_no;

    @ApiModelProperty(value = "库位")
    private String stock;

    @ApiModelProperty(value = "工单")
    private String wo;

    @ApiModelProperty(value = "收货时间")
    private String production_date;

    @ApiModelProperty(value = "工厂")
    private String plant;

    @ApiModelProperty(value = "创建人")
    private String qa_sign;

    @ApiModelProperty(value = "uidid")
    private String uid_id;
}
