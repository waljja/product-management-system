package com.ktg.mes.fg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktg.common.annotation.Excel;
import com.ktg.common.core.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

/**
 * 成品库存表对象 fg_inventory
 * @author JiangTiangming
 * @date 2023-05-05
 */
public class FgInventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 型号 */
    @Excel(name = "型号")
    private String partnumber;

    /** po */
    @Excel(name = "po")
    private String po;

    /** 批次 */
    @Excel(name = "批次")
    private String batch;

    /** UID */
    @Excel(name = "UID")
    private String uid;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 库位号 */
    @Excel(name = "库位号")
    private String stock;

    /** UID状态 */
    @Excel(name = "UID状态")
    private Integer status;

    /** 工单 */
    @Excel(name = "工单")
    private String wo;

    /** 收货时间 */
    @JsonProperty("recTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date recTime;

    /** 创建时间 */
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;

    /** 工厂 */
    @Excel(name = "工厂")
    private Long plant;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUser;

    /** 送检单id */
    @Excel(name = "送检单id")
    private Long uidId;

    /** 回仓原因 */
    @Excel(name = "回仓原因")
    private String rollbackReason;

    @Excel(name = "同PN PO总数量")
    private Long sumQuantity;

    public Long getSumQuantity() {
        return sumQuantity;
    }

    public void setSumQuantity(Long sumQuantity) {
        this.sumQuantity = sumQuantity;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setPartnumber(String partnumber)
    {
        this.partnumber = partnumber;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getPartnumber()
    {
        return partnumber;
    }
    public void setBatch(String batch)
    {
        this.batch = batch;
    }

    public String getBatch()
    {
        return batch;
    }
    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getUid()
    {
        return uid;
    }
    public void setQuantity(Long quantity)
    {
        this.quantity = quantity;
    }

    public Long getQuantity()
    {
        return quantity;
    }
    public void setStock(String stock)
    {
        this.stock = stock;
    }

    public String getStock()
    {
        return stock;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public String getWo() {
        return wo;
    }

    public void setWo(String wo) {
        this.wo = wo;
    }

    public Date getRecTime() {
        return recTime;
    }

    public void setRecTime(Date recTime) {
        this.recTime = recTime;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setPlant(Long plant)
    {
        this.plant = plant;
    }

    public Long getPlant()
    {
        return plant;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public void setUidId(Long uidId)
    {
        this.uidId = uidId;
    }

    public Long getUidId()
    {
        return uidId;
    }

    public String getRollbackReason() {
        return rollbackReason;
    }

    public void setRollbackReason(String rollbackReason) {
        this.rollbackReason = rollbackReason;
    }

    @Override
    public String toString() {
        return "FgInventory{" +
                "id=" + id +
                ", partnumber='" + partnumber + '\'' +
                ", po='" + po + '\'' +
                ", batch='" + batch + '\'' +
                ", uid='" + uid + '\'' +
                ", quantity=" + quantity +
                ", stock='" + stock + '\'' +
                ", status=" + status +
                ", wo='" + wo + '\'' +
                ", recTime=" + recTime +
                ", createTime=" + createTime +
                ", plant=" + plant +
                ", createUser='" + createUser + '\'' +
                ", uidId=" + uidId +
                ", rollbackReason='" + rollbackReason + '\'' +
                ", sumQuantity=" + sumQuantity +
                '}';
    }
}
