package com.honortone.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *     送检单数据封装类
 *     变量名需与数据库字段名一致（因使用了@TableName("FG_SealNoInfo")同步数据库表字段），不区分大小写和个数
 * </p>
 *
 * @author 江庭明
 * @since 2023-03-08
 */

//@TableName("FG_SealNoInfo") //数据库表名
//@ApiModel(value = "SealNoInfo对象", description = "保存走货信息表封装类")
@Data
public class SealNoInfo {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "走货单")
    private String shipmentNo;

    @ApiModelProperty(value = "型号")
    private String pn;
    @ApiModelProperty(value = "车牌")
    private String carNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "鸿通封条")
    private String sealNo;

    @ApiModelProperty(value = "货柜封条")
    private String vansealNo;

    @ApiModelProperty(value = "货柜号")
    private String containerNo;

    @ApiModelProperty(value = "文件存放路径")
    private String filepath;

    @ApiModelProperty(value = "文件名")
    private String filename;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建日期")
    private String createTime;

    @ApiModelProperty(value = "自动过账311")
    private String autoTransfer;

}
