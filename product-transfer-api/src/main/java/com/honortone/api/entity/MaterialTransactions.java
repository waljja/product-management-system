package com.honortone.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *     中间表数据封装类
 *     100数据库
 *     变量名需与数据库字段名一致（因使用了@TableName("xTend_MaterialTransactions")同步数据库表字段），不区分大小写和个数
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */

@TableName("xTend_MaterialTransactions")
@ApiModel(value = "MaterialTransactions对象", description = "")
@Data
public class MaterialTransactions {

    @TableId(type= IdType.ASSIGN_ID)
    private String id;
    @ApiModelProperty("批次")
    private String batch;

    @ApiModelProperty("PN")
    private String partnumber;

    private String uid;

    private String quantity;
    @ApiModelProperty("来自库位")
    private String fromstock;

    @ApiModelProperty("去往库位")
    private String tostock;

    @ApiModelProperty("移库人")
    private String transactionuser;

    @TableField(value = "create_date",fill = FieldFill.INSERT)
    private LocalDateTime transactiontime;

    @ApiModelProperty("提交状态")
    private String recordstatus;

    @ApiModelProperty("入库类型")
    private String transactiontype;

    @ApiModelProperty("")
    private String rfctemplateid;

    @ApiModelProperty("工厂")
    private String plant;


    @ApiModelProperty("")
    private String po_number;


    @ApiModelProperty("")
    private String refdocno;

    @ApiModelProperty("")
    private String docheader;

    @ApiModelProperty("")
    private String erpfromstock;

    @ApiModelProperty("")
    private String erptostock;




}
