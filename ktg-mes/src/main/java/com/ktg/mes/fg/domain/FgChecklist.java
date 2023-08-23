package com.ktg.mes.fg.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktg.common.annotation.Excel;
import com.ktg.common.core.domain.BaseEntity;

/**
 * 成品送检单对象 fg_checklist
 * 
 * @author JiangTiangming
 * @date 2023-03-18
 */
public class FgChecklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 工厂 */
    @Excel(name = "工厂")
    private String plant;

    /** 型号 */
    @Excel(name = "型号")
    private String pn;

    /** UID */
    @Excel(name = "UID")
    private String uid;

    /** 生产线 */
    @Excel(name = "生产线")
    private String line;

    /** 工单 */
    @Excel(name = "工单")
    private String wo;

    /** PO */
    @Excel(name = "PO")
    private String po;

    /** SAP中库位 */
    @Excel(name = "fromstock")
    private String fromStock;

    /** 卡板号 */
    @Excel(name = "卡板号")
    private String palletNo;

    /** 卡板号 */
    @Excel(name = "卡板数")
    private String palletItems;

    /** 批量 */
    @Excel(name = "批量")
    private Long batchQty;

    /** 数量 */
    @Excel(name = "数量")
    private Long uidNo;

    /** 过账编号 */
    @Excel(name = "过账编号")
    private String sap101;

    /** 过账时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过账时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date productionDate;

    /** 批次 */
    @Excel(name = "批次")
    private String batch;

    /** QA签署人 */
    @Excel(name = "QA签署人")
    private String qaSign;

    /** QA检验结果 */
    @Excel(name = "QA检验结果")
    private Integer qaResult;

    /** 客户验查结果 */
    @Excel(name = "客户验查结果")
    private String customerResult;

    /** 打印时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "打印时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdate;

    /** 旧UID */
    @Excel(name = "旧UID")
    private String oldUid;

    /** 成品型号 */
    @Excel(name = "成品型号")
    private String pn660;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 打印人 */
    @Excel(name = "打印人")
    private String createUser;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updateUser;

    /** 条码格式 */
    @Excel(name = "条码格式")
    private String barcodeFormart;

    /** 条码地址 */
    @Excel(name = "条码地址")
    private String barcodeUrl;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    public String getPalletItems() {
        return palletItems;
    }

    public void setPalletItems(String palletItems) {
        this.palletItems = palletItems;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPlant(String plant) 
    {
        this.plant = plant;
    }

    public String getPlant() 
    {
        return plant;
    }
    public void setPn(String pn) 
    {
        this.pn = pn;
    }

    public String getPn() 
    {
        return pn;
    }
    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }
    public void setLine(String line) 
    {
        this.line = line;
    }

    public String getLine() 
    {
        return line;
    }
    public void setWo(String wo) 
    {
        this.wo = wo;
    }

    public String getWo() 
    {
        return wo;
    }
    public void setPo(String po) 
    {
        this.po = po;
    }

    public String getPo() 
    {
        return po;
    }

    public String getFromStock() {
        return fromStock;
    }

    public void setFromStock(String fromStock) {
        this.fromStock = fromStock;
    }

    public void setPalletNo(String palletNo)
    {
        this.palletNo = palletNo;
    }

    public String getPalletNo() 
    {
        return palletNo;
    }
    public void setBatchQty(Long batchQty) 
    {
        this.batchQty = batchQty;
    }

    public Long getBatchQty() 
    {
        return batchQty;
    }
    public void setUidNo(Long uidNo) 
    {
        this.uidNo = uidNo;
    }

    public Long getUidNo() 
    {
        return uidNo;
    }
    public void setSap101(String sap101) 
    {
        this.sap101 = sap101;
    }

    public String getSap101() 
    {
        return sap101;
    }
    public void setProductionDate(Date productionDate) 
    {
        this.productionDate = productionDate;
    }

    public Date getProductionDate() 
    {
        return productionDate;
    }
    public void setBatch(String batch) 
    {
        this.batch = batch;
    }

    public String getBatch() 
    {
        return batch;
    }
    public void setQaSign(String qaSign) 
    {
        this.qaSign = qaSign;
    }

    public String getQaSign() 
    {
        return qaSign;
    }
    public void setQaResult(Integer qaResult) 
    {
        this.qaResult = qaResult;
    }

    public Integer getQaResult() 
    {
        return qaResult;
    }
    public void setCustomerResult(String customerResult) 
    {
        this.customerResult = customerResult;
    }

    public String getCustomerResult() 
    {
        return customerResult;
    }
    public void setCreatedate(Date createdate) 
    {
        this.createdate = createdate;
    }

    public Date getCreatedate() 
    {
        return createdate;
    }
    public void setOldUid(String oldUid) 
    {
        this.oldUid = oldUid;
    }

    public String getOldUid() 
    {
        return oldUid;
    }
    public void setPn660(String pn660) 
    {
        this.pn660 = pn660;
    }

    public String getPn660() 
    {
        return pn660;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setCreateUser(String createUser) 
    {
        this.createUser = createUser;
    }

    public String getCreateUser() 
    {
        return createUser;
    }
    public void setUpdateUser(String updateUser) 
    {
        this.updateUser = updateUser;
    }

    public String getUpdateUser() 
    {
        return updateUser;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
    }

    public String getBarcodeUrl() {
        return barcodeUrl;
    }

    public void setBarcodeUrl(String barcodeUrl) {
        this.barcodeUrl = barcodeUrl;
    }

    public String getBarcodeFormart() {
        return barcodeFormart;
    }

    public void setBarcodeFormart(String barcodeFormart) {
        this.barcodeFormart = barcodeFormart;
    }

    @Override
    public String toString() {
        return "FgChecklist{" +
                "id=" + id +
                ", plant='" + plant + '\'' +
                ", pn='" + pn + '\'' +
                ", uid='" + uid + '\'' +
                ", line='" + line + '\'' +
                ", wo='" + wo + '\'' +
                ", po='" + po + '\'' +
                ", fromStock='" + fromStock + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", palletItems=" + palletItems +
                ", batchQty=" + batchQty +
                ", uidNo=" + uidNo +
                ", sap101='" + sap101 + '\'' +
                ", productionDate=" + productionDate +
                ", batch='" + batch + '\'' +
                ", qaSign='" + qaSign + '\'' +
                ", qaResult=" + qaResult +
                ", customerResult='" + customerResult + '\'' +
                ", createdate=" + createdate +
                ", oldUid='" + oldUid + '\'' +
                ", pn660='" + pn660 + '\'' +
                ", status=" + status +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", barcodeFormart='" + barcodeFormart + '\'' +
                ", barcodeUrl='" + barcodeUrl + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}
