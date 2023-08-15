package com.ktg.mes.fg.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ktg.common.annotation.Excel;
import com.ktg.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 封条装车信息对象 fg_sealnoinfo
 *
 * @author Tingming Jiang
 * @date 2023-06-14
 */
public class FgSealnoinfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 走货编号 */
    @Excel(name = "走货编号")
    private String shipmentNo;

    /** 型号 */
    @Excel(name = "型号")
    private String pn;

    /** 封条号 */
    @Excel(name = "封条号")
    private String sealNo;

    /** 货柜封条 */
    @Excel(name = "货柜封条")
    private String vansealNo;

    /** 货柜号 */
    @Excel(name = "货柜号")
    private String containerNo;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 车号 */
    @Excel(name = "车号")
    private String carNo;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filepath;

    /** 文件名 */
    @Excel(name = "文件名")
    private String filename;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUser;

    @Excel(name = "分页起始")
    private Integer pageNum2;

    @Excel(name = "每页总数")
    private Integer pageSize2;
    /** 创建时间 */
    @Excel(name = "创建时间")
    private Date createTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setShipmentNo(String shipmentNo)
    {
        this.shipmentNo = shipmentNo;
    }

    public String getShipmentNo()
    {
        return shipmentNo;
    }
    public void setSealNo(String sealNo)
    {
        this.sealNo = sealNo;
    }

    public String getSealNo()
    {
        return sealNo;
    }
    public void setVansealNo(String vansealNo)
    {
        this.vansealNo = vansealNo;
    }

    public String getVansealNo()
    {
        return vansealNo;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setCarNo(String carNo)
    {
        this.carNo = carNo;
    }

    public String getCarNo()
    {
        return carNo;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPageNum2() {
        return pageNum2;
    }

    public void setPageNum2(Integer pageNum2) {
        this.pageNum2 = pageNum2;
    }

    public Integer getPageSize2() {
        return pageSize2;
    }

    public void setPageSize2(Integer pageSize2) {
        this.pageSize2 = pageSize2;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    @Override
    public String toString() {
        return "FgSealnoinfo{" +
                "id=" + id +
                ", shipmentNo='" + shipmentNo + '\'' +
                ", pn='" + pn + '\'' +
                ", sealNo='" + sealNo + '\'' +
                ", vansealNo='" + vansealNo + '\'' +
                ", containerNo='" + containerNo + '\'' +
                ", status=" + status +
                ", carNo='" + carNo + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filename='" + filename + '\'' +
                ", createUser='" + createUser + '\'' +
                ", pageNum2=" + pageNum2 +
                ", pageSize2=" + pageSize2 +
                ", createTime=" + createTime +
                '}';
    }
}
