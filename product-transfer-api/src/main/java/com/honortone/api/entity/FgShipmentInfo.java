package com.honortone.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktg.common.annotation.Excel;

import java.util.Date;

/**
 * SAP PMC、船务确认表对象 FG_ShipmentInfo
 *
 * @author JiangTiangming
 * @date 2023-04-19
 */
public class FgShipmentInfo {

    private Long id;

    /**
     * 工厂
     */
    @Excel(name = "工厂")
    private String plant;

    /**
     * sap型号
     */
    @Excel(name = "sap型号")
    private String SapPn;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private Integer status;

    /**
     * 数量
     */
    @Excel(name = "数量")
    private Long Quantity;

    /**
     * 卡板数
     */
    @Excel(name = "卡板数")
    private Long pelletQty;

    /**
     * 最后确认部门
     */
    @Excel(name = "最后确认部门")
    private String LastComfirm;

    /**
     * 走货单
     */
    @Excel(name = "走货单")
    private String ShipmentNO;

    /**
     * PO
     */
    @Excel(name = "po")
    private String po;

    /**
     * 车号
     */
    @Excel(name = "车号")
    private String CarNo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date CreateTime;

    /**
     * 走货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "走货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ShipmentDate;

    /**
     * 车辆预计到达时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "车辆预计到达时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date arriveDate;

    /**
     * 走货车辆
     */
    @Excel(name = "走货车辆")
    private String Car;

    /**
     * 走货类型
     */
    @Excel(name = "走货类型")
    private String ShipmentType;

    /**
     * Remark
     */
    @Excel(name = "Remark")
    private String Remark;

    /**
     * 箱数
     */
    @Excel(name = "箱数")
    private Long BoxQty;

    /**
     * PCS/箱
     */
    @Excel(name = "PCS/箱")
    private Long PcsQty;

    /**
     * 客户代码
     */
    @Excel(name = "客户代码")
    private String clientCode;

    /**
     * 客户PN
     */
    @Excel(name = "客户PN")
    private String clientPN;

    /**
     * 走货地点
     */
    @Excel(name = "走货地点")
    private String shipmentPlace;

    /**
     * 变动日期
     */
    @Excel(name = "PCS/变动日期")
    private String updateDate;

    /**
     * 变动时间
     */
    @Excel(name = "PCS/变动时间")
    private String updateTime;


    public String getLastComfirm() {
        return LastComfirm;
    }

    public void setLastComfirm(String lastComfirm) {
        LastComfirm = lastComfirm;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public Date getShipmentDate() {
        return ShipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        ShipmentDate = shipmentDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getCar() {
        return Car;
    }

    public void setCar(String car) {
        Car = car;
    }

    public String getShipmentType() {
        return ShipmentType;
    }

    public void setShipmentType(String shipmentType) {
        ShipmentType = shipmentType;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getSapPn() {
        return SapPn;
    }

    public void setSapPn(String sapPn) {
        SapPn = sapPn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
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

    public String getShipmentNO() {
        return ShipmentNO;
    }

    public void setShipmentNO(String shipmentNO) {
        ShipmentNO = shipmentNO;
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

    public Long getBoxQty() {
        return BoxQty;
    }

    public void setBoxQty(Long boxQty) {
        BoxQty = boxQty;
    }

    public Long getPcsQty() {
        return PcsQty;
    }

    public void setPcsQty(Long pcsQty) {
        PcsQty = pcsQty;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Long getPelletQty() {
        return pelletQty;
    }

    public void setPelletQty(Long pelletQty) {
        this.pelletQty = pelletQty;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientPN() {
        return clientPN;
    }

    public void setClientPN(String clientPN) {
        this.clientPN = clientPN;
    }

    public String getShipmentPlace() {
        return shipmentPlace;
    }

    public void setShipmentPlace(String shipmentPlace) {
        this.shipmentPlace = shipmentPlace;
    }

    @Override
    public String toString() {
        return "FgShipmentInfo{" +
                "id=" + id +
                ", plant='" + plant + '\'' +
                ", SapPn='" + SapPn + '\'' +
                ", status=" + status +
                ", Quantity=" + Quantity +
                ", pelletQty=" + pelletQty +
                ", LastComfirm='" + LastComfirm + '\'' +
                ", ShipmentNO='" + ShipmentNO + '\'' +
                ", po='" + po + '\'' +
                ", CarNo='" + CarNo + '\'' +
                ", CreateTime=" + CreateTime +
                ", ShipmentDate=" + ShipmentDate +
                ", arriveDate=" + arriveDate +
                ", Car='" + Car + '\'' +
                ", ShipmentType='" + ShipmentType + '\'' +
                ", Remark='" + Remark + '\'' +
                ", BoxQty=" + BoxQty +
                ", PcsQty=" + PcsQty +
                ", clientCode='" + clientCode + '\'' +
                ", clientPN='" + clientPN + '\'' +
                ", shipmentPlace='" + shipmentPlace + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
