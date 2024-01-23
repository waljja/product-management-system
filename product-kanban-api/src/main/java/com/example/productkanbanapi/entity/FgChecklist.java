package com.example.productkanbanapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName fg_checklist
 */
@TableName(value ="fg_checklist")
@Data
public class FgChecklist implements Serializable {

    private Long id;

    private String plant;

    private String pn;

    private String uid;

    private String line;

    private String wo;

    private String po;

    private String fromstock;

    private String palletNo;

    private Double batchQty;

    private Double uidNo;

    private String sap101;

    private Date productionDate;

    private String batch;

    private String remark;

    private String qaSign;

    private Boolean qaResult;

    private String customerResult;

    private Date createdate;

    private String oldUid;

    private String pn660;

    private byte[] status;

    private String clientCode;

    private String createUser;

    private String updateUser;

    private Date updateDate;

    private String barcodeUrl;

    private String barcodeFormart;

    private String palletItems;

    private static final long serialVersionUID = 1L;

}