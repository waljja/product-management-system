package com.ktg.mes.fg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktg.common.annotation.Excel;

import java.util.Date;

/**
 * TO明细 FG_ToList
 *
 * @author JiangTiangming
 * @date 2023-04-20
 */
public class FgToList {

    private Long id;

    /** 备料单/欠料单 */
    @JsonProperty("To_No")
    @Excel(name = "备料单/欠料单")
    private String To_No;

    /** UID */
    @Excel(name = "UID")
    private String uid;

    /** 替换UID */
    @Excel(name = "UID")
    private String replace_uid;

    /** 型号 */
    @Excel(name = "型号")
    private String pn;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 数量(库存） */
    @JsonProperty("Quantity")
    @Excel(name = "数量")
    private Long Quantity;

    /** To管理数量 */
    @Excel(name = "To管理数量")
    private Long sap_qty;

    /** PO */
    @Excel(name = "PO")
    private String po;

    /** 批次 */
    @Excel(name = "批次")
    private String batch;

    /** 客户批次 */
    @Excel(name = "客户批次")
    private String clientBatch;

    /** 客户【批次数量(库存） */
    @JsonProperty("clientQty")
    @Excel(name = "数量")
    private Long clientQty;

    /** 走货区 */
    @Excel(name = "走货区")
    private String areaStock;

    /** 库位 */
    @Excel(name = "库位")
    private String stock;

    /** 走货编号 */
    @Excel(name = "走货编号")
    private String ShipmentNO;

    /** 创建时间 */
    @JsonProperty("createTime")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 拣货时间 */
    @JsonProperty("updateTime")
    @Excel(name = "拣货时间")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTo_No() {
        return To_No;
    }

    public void setTo_No(String to_No) {
        To_No = to_No;
    }

    public String getUid() {
        return uid;
    }

    public String getReplace_uid() {
        return replace_uid;
    }

    public void setReplace_uid(String replace_uid) {
        this.replace_uid = replace_uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getQuantity() {
        return Quantity;
    }

    public void setQuantity(Long quantity) {
        Quantity = quantity;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Long getSap_qty() {
        return sap_qty;
    }

    public void setSap_qty(Long sap_qty) {
        this.sap_qty = sap_qty;
    }

    public String getAreaStock() {
        return areaStock;
    }

    public void setAreaStock(String areaStock) {
        this.areaStock = areaStock;
    }

    public String getShipmentNO() {
        return ShipmentNO;
    }

    public void setShipmentNO(String shipmentNO) {
        ShipmentNO = shipmentNO;
    }

    public String getClientBatch() {
        return clientBatch;
    }

    public void setClientBatch(String clientBatch) {
        this.clientBatch = clientBatch;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getClientQty() {
        return clientQty;
    }

    public void setClientQty(Long clientQty) {
        this.clientQty = clientQty;
    }

    @Override
    public String toString() {
        return "FgToList{" +
                "id=" + id +
                ", To_No='" + To_No + '\'' +
                ", uid='" + uid + '\'' +
                ", replace_uid='" + replace_uid + '\'' +
                ", pn='" + pn + '\'' +
                ", status=" + status +
                ", Quantity=" + Quantity +
                ", sap_qty=" + sap_qty +
                ", po='" + po + '\'' +
                ", batch='" + batch + '\'' +
                ", clientBatch='" + clientBatch + '\'' +
                ", clientQty=" + clientQty +
                ", areaStock='" + areaStock + '\'' +
                ", stock='" + stock + '\'' +
                ", ShipmentNO='" + ShipmentNO + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
