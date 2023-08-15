package com.honortone.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 接收前端请求参数（字段名与前端参数字段名一致）
 */
@Data
public class SaveInfoDto {

    @ApiModelProperty(value = "走货单")
    private String shipmentNo;

    @ApiModelProperty(value = "车牌")
    private String carno;

    @ApiModelProperty(value = "货柜")
    private String container;

    @ApiModelProperty(value = "型号")
    private String pn;

    @ApiModelProperty(value = "鸿通封条")
    private String ht_seal;

    @ApiModelProperty(value = "货柜封条")
    private String hg_seal;

    @ApiModelProperty(value = "文件存放路径")
    private String path;

    @ApiModelProperty(value = "文件名")
    private String filename;

    @ApiModelProperty(value = "创建人")
    private String createuser;

    @ApiModelProperty(value = "创建日期")
    private String createtime;
}
