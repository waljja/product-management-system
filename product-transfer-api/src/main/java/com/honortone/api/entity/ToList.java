package com.honortone.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class ToList implements Serializable {

    private Long id;

    @ApiModelProperty("备货单")
    private String toNo;

    @ApiModelProperty("型号")
    private String pn;

    @ApiModelProperty("po")
    private String po;

    @ApiModelProperty("批次")
    private String batch;

    @ApiModelProperty("UID")
    private String uid;

    @ApiModelProperty("库位")
    private String stock;

    @ApiModelProperty("工厂")
    private String plant;

    @ApiModelProperty("工单")
    private String wo;

    @ApiModelProperty("数量")
    private long quantity;

    @ApiModelProperty("批量")
    private long batchQty;

    @ApiModelProperty("状态")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getBatchQty() {
        return batchQty;
    }

    public void setBatchQty(long batchQty) {
        this.batchQty = batchQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getWo() {
        return wo;
    }

    public void setWo(String wo) {
        this.wo = wo;
    }

    @Override
    public String toString() {
        return "ToList{" +
                "id=" + id +
                ", toNo='" + toNo + '\'' +
                ", pn='" + pn + '\'' +
                ", po='" + po + '\'' +
                ", batch='" + batch + '\'' +
                ", uid='" + uid + '\'' +
                ", stock='" + stock + '\'' +
                ", plant='" + plant + '\'' +
                ", wo='" + wo + '\'' +
                ", quantity=" + quantity +
                ", batchQty=" + batchQty +
                ", status=" + status +
                '}';
    }
}
