package com.ktg.mes.fg.domain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktg.common.annotation.Excel;

import java.util.Date;

/**
 * 获取SAP走货资料总数、走货单、PO、PN
 *
 * @author JiangTiangming
 * @date 2023-04-28
 */
public class ShipmentPart {

    /** 走货单 */
    @Excel(name = "走货单")
    private String ShipmentNO;

    /** PN */
    @Excel(name = "PN")
    private String pn;

    /** PO */
    @Excel(name = "PO")
    private String po;

    /** 总数 */
    @Excel(name = "总数")
    private long batchsum;

    /** 车号 */
    @Excel(name = "车号")
    private String carno;

    @Excel(name = "工厂")
    private String plant;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "走货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shipmentDate;

    public String getShipmentNO() {
        return ShipmentNO;
    }

    public void setShipmentNO(String shipmentNO) {
        ShipmentNO = shipmentNO;
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

    public long getBatchsum() {
        return batchsum;
    }

    public void setBatchsum(long batchsum) {
        this.batchsum = batchsum;
    }

    public String getCarno() {
        return carno;
    }

    public void setCarno(String carno) {
        this.carno = carno;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    @Override
    public String toString() {
        return "ShipmentPart{" +
                "ShipmentNO='" + ShipmentNO + '\'' +
                ", pn='" + pn + '\'' +
                ", po='" + po + '\'' +
                ", batchsum=" + batchsum +
                ", carno='" + carno + '\'' +
                ", plant='" + plant + '\'' +
                ", shipmentDate=" + shipmentDate +
                '}';
    }
}
