package com.ktg.mes.fg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktg.common.annotation.Excel;
import java.util.Date;

/**
 * TO管理 FG_TOS
 *
 * @author JiangTiangming
 * @date 2023-04-18
 */
public class FgTos {

    private Long id;

    /** 备料单/欠料单 */
    @Excel(name = "备料单/欠料单")
    private String To_No;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 批量 */
    @Excel(name = "走货单总数量")
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

//    /** PN */
//    @Excel(name = "PN")
//    private String SapPn;
//
//    /** PO */
//    @Excel(name = "PO")
//    private String po;


    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date CreateTime;

//    public String getSapPn() {
//        return SapPn;
//    }
//
//    public void setSapPn(String sapPn) {
//        SapPn = sapPn;
//    }
//
//    public String getPo() {
//        return po;
//    }
//
//    public void setPo(String po) {
//        this.po = po;
//    }

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

    @Override
    public String toString() {
        return "FgTos{" +
                "id=" + id +
                ", To_No='" + To_No + '\'' +
                ", status=" + status +
                ", sap_qty=" + sap_qty +
                ", ShipmentNO='" + ShipmentNO + '\'' +
                ", plant='" + plant + '\'' +
                ", CarNo='" + CarNo + '\'' +
                ", CreateTime=" + CreateTime +
                '}';
    }
}
