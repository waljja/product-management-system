package com.honortone.api.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

//@TableName("FG_ShipmentinfoByhand") //数据库表名
@ApiModel(value = "ShipmentinfoByhand对象", description = "手动上传走货资料信息")
//@Data
public class ShipmentInfoByhand {
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty(value = "客户")
    private String client;

    @ApiModelProperty(value = "SAP型号")
    private String sapPn;

    @ApiModelProperty(value = "数量")
    private long quantity;

    @ApiModelProperty(value = "走货日期")
    private Date recTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "车号")
    private String carNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSapPn() {
        return sapPn;
    }

    public void setSapPn(String sapPn) {
        this.sapPn = sapPn;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Date getRecTime() {
        return recTime;
    }

    public void setRecTime(Date recTime) {
        this.recTime = recTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    @Override
    public String toString() {
        return "ShipmentInfoByhand{" +
                "id='" + id + '\'' +
                ", client='" + client + '\'' +
                ", sapPn='" + sapPn + '\'' +
                ", quantity=" + quantity +
                ", recTime=" + recTime +
                ", status=" + status +
                ", carNo='" + carNo + '\'' +
                '}';
    }
}
