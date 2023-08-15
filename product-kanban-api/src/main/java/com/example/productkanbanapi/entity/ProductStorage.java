package com.example.productkanbanapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * ProductStorage
 *
 * @author 丁国钊
 * @date 2023-08-15-10:18
 */
@Data
public class ProductStorage {

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
