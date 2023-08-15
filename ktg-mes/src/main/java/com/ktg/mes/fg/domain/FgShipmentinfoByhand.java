package com.ktg.mes.fg.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ktg.common.annotation.Excel;
import com.ktg.common.core.domain.BaseEntity;

/**
 * 手动导入走货资料 对象 fg_shipmentinfo_byhand
 *
 * @author JiangTingming
 * @date 2023-05-17
 */
public class FgShipmentinfoByhand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 客户 */
    @Excel(name = "客户")
    private String client;

    /** SAP型号 */
    @Excel(name = "SAP型号")
    private String sapPn;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 走货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "走货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date recTime;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String carNo;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setClient(String client)
    {
        this.client = client;
    }

    public String getClient()
    {
        return client;
    }
    public void setSapPn(String sapPn)
    {
        this.sapPn = sapPn;
    }

    public String getSapPn()
    {
        return sapPn;
    }
    public void setQuantity(Long quantity)
    {
        this.quantity = quantity;
    }

    public Long getQuantity()
    {
        return quantity;
    }
    public void setRecTime(Date recTime)
    {
        this.recTime = recTime;
    }

    public Date getRecTime()
    {
        return recTime;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    @Override
    public String toString() {
        return "FgShipmentinfoByhand{" +
                "id=" + id +
                ", client='" + client + '\'' +
                ", sapPn='" + sapPn + '\'' +
                ", quantity=" + quantity +
                ", recTime=" + recTime +
                ", status=" + status +
                ", carNo='" + carNo + '\'' +
                '}';
    }
}
