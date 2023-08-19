package com.example.productkanbanapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * XtendMaterialtransactions
 *
 * @author 丁国钊
 * @date 2023-08-18-16:14
 */
@Data@TableName(value ="xTend_MaterialTransactions")

public class XtendMaterialtransactions {

    /**
     * id
     */
    @TableId
    private Object transactionhistoryid;

    /**
     * 批次
     */
    private String batch;

    /**
     * 型号
     */
    private String partnumber;

    /**
     * uid
     */
    private String uid;

    /**
     * 数量
     */
    private Double quantity;

    /**
     *
     */
    private String uom;

    /**
     * 发出仓位
     */
    private String fromstock;

    /**
     * 接收仓位
     */
    private String tostock;

    /**
     * 过账用户
     */
    private String transactionuser;

    /**
     * 过账时间
     */
    private Date transactiontime;

    /**
     *
     */
    private Integer recordstatus;

    /**
     * 过账类型
     */
    private String transactiontype;

    /**
     *
     */
    private String transactionreason;

    /**
     *
     */
    private String rfctemplateid;

    /**
     * 过账编号
     */
    private String refdocno;

    /**
     * 过账年份
     */
    private String docheader;

    /**
     *
     */
    private String docheader1;

    /**
     * 厂区
     */
    private String plant;

    /**
     *
     */
    private String vendor;

    /**
     *
     */
    private String specialstock;

    /**
     *
     */
    private String fromstocklocation;

    /**
     *
     */
    private String tostocklocation;

    /**
     *
     */
    private String erpfromstock;

    /**
     *
     */
    private String erptostock;

    /**
     *
     */
    private String iPrueflos;

    /**
     *
     */
    private String iVcode;

    /**
     *
     */
    private String grRcpt;

    /**
     *
     */
    private String costcenter;

    /**
     *
     */
    private String indProposeQuanx;

    /**
     *
     */
    private String moveMat;

    /**
     *
     */
    private String moveBatch;

    /**
     *
     */
    private String movePlant;

    /**
     *
     */
    private String poItem;

    /**
     *
     */
    private String poNumber;

    /**
     *
     */
    private String orderid;

    /**
     *
     */
    private String orderItno;

    /**
     *
     */
    private String refDoc;

    /**
     *
     */
    private String refDocYr;

    /**
     *
     */
    private String grn;

    /**
     *
     */
    private String toName;

    /**
     *
     */
    private String remarks351;

    /**
     *
     */
    private String insert311Option;

    /**
     *
     */
    private String poToPo;

    /**
     *
     */
    private Double qty261;

    /**
     *
     */
    private String smartcarStatus;

    /**
     * 货架号
     */
    private String carNo;

    /**
     * 验证时间
     */
    private Date validdate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
