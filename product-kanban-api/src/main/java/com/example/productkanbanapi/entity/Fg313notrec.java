package com.example.productkanbanapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName FG_313NotRec
 */
@Data
@TableName(value ="FG_313NotRec")
public class Fg313notrec implements Serializable {

    private Object id;

    private String partnumber;

    private String poNumber;

    private String batch;

    private String uid;

    private String status;

    private Double quantity;

    private Integer plant;

    private static final long serialVersionUID = 1L;

}