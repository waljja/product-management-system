package com.honortone.api.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktg.common.annotation.Excel;

import java.util.Date;

/**
 * TO明细表值
 * 数据来源：SAP确认数据整合ShipmentPart关联库存表Fg_Inventory
 *
 * @author JiangTiangming
 * @date 2023-04-20
 */
public class FgTosAndTosListDto {

    private Long id;

    /** 备料单/欠料单 */
    @Excel(name = "备料单/欠料单")
    private String To_No;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** UID */
    @Excel(name = "UID")
    private String uid;

    /** 数量 */
    @Excel(name = "数量")
    private Long Quantity;

    /** 库存表同一PO  PN的总数量 */
    @Excel(name = "数量")
    private Long sum_uidno;

    /** 从SAP导入时的数量 */
    @Excel(name = "SAP数量")
    private Long sap_qty;

    /** 走货单 */
    @Excel(name = "走货单")
    private String ShipmentNO;

    /** 工厂 */
    @Excel(name = "工厂")
    private String plant;

    /** 车号 */
    @Excel(name = "车号")
    private String CarNo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date CreateTime;

    /** 型号 */
    @Excel(name = "型号")
    private String pn;

    /** PO */
    @Excel(name = "PO")
    private String po;

    /** 批次 */
    @Excel(name = "批次")
    private String batch;

    /** 库位 */
    @Excel(name = "库位")
    private String stock;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getQuantity() {
        return Quantity;
    }

    public void setQuantity(Long quantity) {
        Quantity = quantity;
    }

    public Long getSap_qty() {
        return sap_qty;
    }

    public void setSap_qty(Long sap_qty) {
        this.sap_qty = sap_qty;
    }

    public String getShipmentNO() {
        return ShipmentNO;
    }

    public void setShipmentNO(String shipmentNO) {
        ShipmentNO = shipmentNO;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getCarNo() {
        return CarNo;
    }

    public void setCarNo(String carNo) {
        CarNo = carNo;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
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

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Long getSum_uidno() {
        return sum_uidno;
    }

    public void setSum_uidno(Long sum_uidno) {
        this.sum_uidno = sum_uidno;
    }

    @Override
    public String toString() {
        return "FgTosAndTosListDto{" +
                "id=" + id +
                ", To_No='" + To_No + '\'' +
                ", status=" + status +
                ", uid='" + uid + '\'' +
                ", Quantity=" + Quantity +
                ", sum_uidno=" + sum_uidno +
                ", sap_qty=" + sap_qty +
                ", ShipmentNO='" + ShipmentNO + '\'' +
                ", plant='" + plant + '\'' +
                ", CarNo='" + CarNo + '\'' +
                ", CreateTime=" + CreateTime +
                ", pn='" + pn + '\'' +
                ", po='" + po + '\'' +
                ", batch='" + batch + '\'' +
                ", stock='" + stock + '\'' +
                '}';
    }
}
