package com.honortone.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ktg.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 *     送检单数据封装类
 *     变量名需与数据库字段名一致（因使用了@TableName("FG_Checklist")同步数据库表字段），不区分大小写和个数
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */
@TableName("fg_checklist") //数据库表名
@ApiModel(value = "Checklist对象", description = "")
@Data
public class CheckList {
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("工厂")
    private String plant;

    @ApiModelProperty("型号")
    private String pn;

    @ApiModelProperty("UID")
    private String uid;

    @ApiModelProperty("生产线")
    private String line;

    @ApiModelProperty("工单")
    private String wo;

    @ApiModelProperty("客户订单编号")
    private String po;

    @ApiModelProperty("卡板号")
    private String pallet_no;

    @ApiModelProperty("批次数量")
    private float batch_qty;

    @ApiModelProperty("uid数量")
    private float uid_no;

    @ApiModelProperty("SAP101过账成功编号")
    private String sap101;

    @ApiModelProperty("SAP过账时间")
    private Date production_date;

    @ApiModelProperty("批次")
    private String batch;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("QA签署人")
    private String qa_sign;

    @ApiModelProperty("QA检验结果")
    private Integer qa_result;

    @ApiModelProperty("旧UID")
    private String old_uid;

    @ApiModelProperty("客户检验结果")
    private String customer_result;

    /** 成品型号 */
    @ApiModelProperty("660成品型号")
    private String pn660;

    /** 客户编号 */
    @ApiModelProperty("客户编号")
    private String client_code;

}
