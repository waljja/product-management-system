package com.honortone.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktg.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

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

    @ApiModelProperty("客户批次")
    private String clientBatch;

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

    @ApiModelProperty("客户批次数量")
    private long clientQty;

    @ApiModelProperty("批量")
    private long batchQty;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("客户贴纸状态")
    private Integer status2;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "拣货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;


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

    public String getClientBatch() {
        return clientBatch;
    }

    public void setClientBatch(String clientBatch) {
        this.clientBatch = clientBatch;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public long getClientQty() {
        return clientQty;
    }

    public void setClientQty(long clientQty) {
        this.clientQty = clientQty;
    }

    public Integer getStatus2() {
        return status2;
    }

    public void setStatus2(Integer status2) {
        this.status2 = status2;
    }

    @Override
    public String toString() {
        return "ToList{" +
                "id=" + id +
                ", toNo='" + toNo + '\'' +
                ", pn='" + pn + '\'' +
                ", po='" + po + '\'' +
                ", batch='" + batch + '\'' +
                ", clientBatch='" + clientBatch + '\'' +
                ", uid='" + uid + '\'' +
                ", stock='" + stock + '\'' +
                ", plant='" + plant + '\'' +
                ", wo='" + wo + '\'' +
                ", quantity=" + quantity +
                ", clientQty=" + clientQty +
                ", batchQty=" + batchQty +
                ", status=" + status +
                ", status2=" + status2 +
                ", updateTime=" + updateTime +
                '}';
    }
}
