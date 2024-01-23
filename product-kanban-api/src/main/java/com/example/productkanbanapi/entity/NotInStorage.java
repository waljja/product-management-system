package com.example.productkanbanapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * ProductStorage
 *
 * @author 丁国钊
 * @date 2023-08-15-10:18
 */
@Data
public class NotInStorage {

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
    private String storageLoc;

    /**
     * 状态
     */
    private String state;

    /**
     * 工单
     */
    private String wo;

    /**
     * 接收时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recTime = null;

    /**
     * 厂区
     */
    private String plant;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
