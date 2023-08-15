package com.honortone.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.objenesis.SpringObjenesis;

/**
 * 接收前端请求参数（字段名与前端参数字段名一致）
 */
@Data
public class MaterialTransationsDto {

    @ApiModelProperty("通过查询获得的pn")
    private String pn;

    @ApiModelProperty("通过查询获得的批次号")
    private String batch;

    @ApiModelProperty("通过查询获得的工厂")
    private String plant;

    @ApiModelProperty("调SAP接口获得的313")
    private String transactiontype;

    @ApiModelProperty("sap101")
    private String sap101;

    @ApiModelProperty("调SAP接口获得的批次总数")
    private float quantity;

    @ApiModelProperty("库位")
    private float kw;

    @ApiModelProperty("提示")
    private String msg;

    @ApiModelProperty("来自库位（313）")
    private String fromstock;

    @ApiModelProperty("去往库位（315）")
    private String tostock;
}
