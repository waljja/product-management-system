package com.honortone.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktg.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BoxInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 关联UID */
    @Excel(name = "关联UID")
    private String uid;

    /** 箱号 */
    @Excel(name = "箱号")
    private String cartonNo;

    /** 工单 */
    @Excel(name = "工单")
    private String wo;

    /** 贴纸数量 */
    @Excel(name = "贴纸数量")
    private Long cartonQty;

    /**  */
    @Excel(name = "")
    private Integer status;

    /** 批次号 */
    @Excel(name = "客户批次号")
    private String Batch;

    /** 过账日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过账日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date productionDate;

    /** 鸿通PN */
    @Excel(name = "鸿通PN")
    private String pn;

    /** 库位 */
    @Excel(name = "库位")
    private String stock;

    /**  */
    @Excel(name = "")
    private String po;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;

    @Override
    public String toString() {
        return "BoxInventory{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", cartonNo='" + cartonNo + '\'' +
                ", wo='" + wo + '\'' +
                ", cartonQty=" + cartonQty +
                ", status=" + status +
                ", Batch='" + Batch + '\'' +
                ", productionDate=" + productionDate +
                ", pn='" + pn + '\'' +
                ", stock='" + stock + '\'' +
                ", po='" + po + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
