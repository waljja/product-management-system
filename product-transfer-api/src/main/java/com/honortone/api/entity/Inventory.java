package com.honortone.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *     成品库存表数据封装类
 *     变量名需与数据库字段名一致（因使用了@TableName("FG_Inventory")同步数据库表字段），不区分大小写和个数
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */
//@TableName("fg_inventory")
@ApiModel(value = "Inventory对象", description = "")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty("型号")
    private String pn;

    @ApiModelProperty("650型号")
    private String pn650;

    @ApiModelProperty("po")
    private String po;

    @ApiModelProperty("clientPO")
    private String clientPO;

    @ApiModelProperty("批次")
    private String batch;

    @ApiModelProperty("UID")
    private String uid;

    @ApiModelProperty("数量")
    private float uid_no;

    @ApiModelProperty("数量")
    private long tagsQuantity;

    @ApiModelProperty("库位号")
    private String stock;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("工单")
    private String wo;

    @ApiModelProperty("生产日期")
    private Date production_date;

    @ApiModelProperty("工厂")
    private String plant;

    @ApiModelProperty("客户编号")
    private String client_code;

    @ApiModelProperty("创建人")
    private String qa_sign;

    @ApiModelProperty("创建时间")
    private LocalDateTime create_date;

    @ApiModelProperty("uid_id")
    private String uid_id;

    @ApiModelProperty("回仓原因")
    private String rollbackReason;

    @ApiModelProperty("生产PO")
    private String proPO;

    public String getPn650() {
        return pn650;
    }

    public void setPn650(String pn650) {
        this.pn650 = pn650;
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

    public float getUid_no() {
        return uid_no;
    }

    public void setUid_no(float uid_no) {
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

    public String getRollbackReason() {
        return rollbackReason;
    }

    public void setRollbackReason(String rollbackReason) {
        this.rollbackReason = rollbackReason;
    }

    public long getTagsQuantity() {
        return tagsQuantity;
    }

    public void setTagsQuantity(long tagsQuantity) {
        this.tagsQuantity = tagsQuantity;
    }

    public String getClientPO() {
        return clientPO;
    }

    public void setClientPO(String clientPO) {
        this.clientPO = clientPO;
    }

    public String getClient_code() {
        return client_code;
    }

    public void setClient_code(String client_code) {
        this.client_code = client_code;
    }

    public String getProPO() {
        return proPO;
    }

    public void setProPO(String proPO) {
        this.proPO = proPO;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", pn='" + pn + '\'' +
                ", pn650='" + pn650 + '\'' +
                ", po='" + po + '\'' +
                ", clientPO='" + clientPO + '\'' +
                ", batch='" + batch + '\'' +
                ", uid='" + uid + '\'' +
                ", uid_no=" + uid_no +
                ", tagsQuantity=" + tagsQuantity +
                ", stock='" + stock + '\'' +
                ", status=" + status +
                ", wo='" + wo + '\'' +
                ", production_date=" + production_date +
                ", plant='" + plant + '\'' +
                ", client_code='" + client_code + '\'' +
                ", qa_sign='" + qa_sign + '\'' +
                ", create_date=" + create_date +
                ", uid_id='" + uid_id + '\'' +
                ", rollbackReason='" + rollbackReason + '\'' +
                ", proPO='" + proPO + '\'' +
                '}';
    }
}
