package com.honortone.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ShipmentAndTos {

    @ApiModelProperty(value = "走货单")
    private String shipmentNo;

    @ApiModelProperty(value = "PN")
    private String pn;

    @ApiModelProperty(value = "备货单")
    private String toNo;

    @ApiModelProperty(value = "车牌号")
    private String carNo;

    @ApiModelProperty(value = "走货日期")
    private Date shipmentDate;
}
