package com.ktg.mes.fg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktg.common.annotation.Excel;
import com.ktg.common.core.domain.BaseEntity;

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
    @Excel(name = "660型号")
    private String partnumber;

    /** 650型号 */
    @Excel(name = "650型号")
    private String pn650;

    /** po */
    @Excel(name = "上架po")
    private String po;

    /** po */
    @Excel(name = "客户po")
    private String clientPO;

    /** 批次 */
    @Excel(name = "库存批次")
    private String batch;

    /** UID */
    @Excel(name = "库存UID")
    private String uid;

    /** 数量 */
    @Excel(name = "库存数量")
    private Long quantity;

    @Excel(name = "贴纸数量")
    private Long tagsQuantity;

    /** 库位号 */
    @Excel(name = "库位号")
    private String stock;

    /** UID状态 */
    @Excel(name = "UID状态")
    private Integer status;

    /** 工单 */
    @Excel(name = "工单")
    private String wo;

    /** 101过账时间 */
    @JsonProperty("recTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产时间", width = 30, dateFormat = "yyyy-MM-dd")
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

    @Excel(name = "生产PO")
    private String proPO;

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

    public Date getCreateTime() {
        return createTime;
    }

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

    public Long getTagsQuantity() {
        return tagsQuantity;
    }

    public void setTagsQuantity(Long tagsQuantity) {
        this.tagsQuantity = tagsQuantity;
    }

    public String getPn650() {
        return pn650;
    }

    public void setPn650(String pn650) {
        this.pn650 = pn650;
    }

    public String getClientPO() {
        return clientPO;
    }

    public void setClientPO(String clientPO) {
        this.clientPO = clientPO;
    }

    public String getProPO() {
        return proPO;
    }

    public void setProPO(String proPO) {
        this.proPO = proPO;
    }

    @Override
    public String toString() {
        return "FgInventory{" +
                "id=" + id +
                ", partnumber='" + partnumber + '\'' +
                ", pn650='" + pn650 + '\'' +
                ", po='" + po + '\'' +
                ", clientPO='" + clientPO + '\'' +
                ", batch='" + batch + '\'' +
                ", uid='" + uid + '\'' +
                ", quantity=" + quantity +
                ", tagsQuantity=" + tagsQuantity +
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
                ", proPO='" + proPO + '\'' +
                '}';
    }
}
