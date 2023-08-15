package com.honortone.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@ApiModel(value = "Inventory_out对象", description = "")
public class InventoryOut implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty("型号")
    private String pn;

    @ApiModelProperty("po")
    private String po;

    @ApiModelProperty("批次")
    private String batch;

    @ApiModelProperty("UID")
    private String uid;

    @ApiModelProperty("数量")
    private Integer uid_no;

    @ApiModelProperty("库位号")
    private String stock;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("工单")
    private String wo;

    @ApiModelProperty("收货时间")
    private Date production_date;

    @ApiModelProperty("工厂")
    private String plant;

    @ApiModelProperty("创建人")
    private String qa_sign;

    @ApiModelProperty("创建时间")
    private LocalDateTime create_date;

    @ApiModelProperty("uid_id")
    private String uid_id;

    @ApiModelProperty("回仓原因")
    private String rollbackReason;

    public String getRollbackReason() {
        return rollbackReason;
    }

    public void setRollbackReason(String rollbackReason) {
        this.rollbackReason = rollbackReason;
    }

    public String getUid_id() {
        return uid_id;
    }

    public void setUid_id(String uid_id) {
        this.uid_id = uid_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getUid_no() {
        return uid_no;
    }

    public void setUid_no(Integer uid_no) {
        this.uid_no = uid_no;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getWo() {
        return wo;
    }

    public void setWo(String wo) {
        this.wo = wo;
    }
    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public Date getProduction_date() {
        return production_date;
    }

    public void setProduction_date(Date production_date) {
        this.production_date = production_date;
    }

    public String getQa_sign() {
        return qa_sign;
    }

    public void setQa_sign(String qa_sign) {
        this.qa_sign = qa_sign;
    }

    public LocalDateTime getCreate_date() {
        return create_date;
    }

    public void setCreate_date(LocalDateTime create_date) {
        this.create_date = create_date;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @Override
    public String toString() {
        return "InventoryOut{" +
                "id=" + id +
                ", pn='" + pn + '\'' +
                ", po='" + po + '\'' +
                ", batch='" + batch + '\'' +
                ", uid='" + uid + '\'' +
                ", uid_no=" + uid_no +
                ", stock='" + stock + '\'' +
                ", status=" + status +
                ", wo='" + wo + '\'' +
                ", production_date=" + production_date +
                ", plant='" + plant + '\'' +
                ", qa_sign='" + qa_sign + '\'' +
                ", create_date=" + create_date +
                ", uid_id='" + uid_id + '\'' +
                ", rollbackReason='" + rollbackReason + '\'' +
                '}';
    }
}