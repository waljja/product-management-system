package com.example.productkanbanapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 成品入库看板实体类
 *
 * @author 丁国钊
 * @date 2023-08-15-8:30
 */
@Data
@TableName(value ="FG_Inventory")
public class Inventory {

    /**
     * UUID
     */
    private Object id;

    /**
     * 型号
     */
    private String partNumber;

    /**
     * 批次
     */
    private String batch;

    /**
     * UID
     */
    private String uid;

    /**
     * 数量
     */
    private Double quantity;

    /**
     * 库存位置
     */
    private String stock;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 工单
     */
    private String wo;

    /**
     * 接收时间
     */
    private Date recTime;

    /**
     * 厂区
     */
    private String plant;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * UID ID
     */
    private String uidId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
