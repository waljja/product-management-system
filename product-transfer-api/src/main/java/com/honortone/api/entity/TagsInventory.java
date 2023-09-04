package com.honortone.api.entity;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ktg.common.annotation.Excel;

/**
 * 【FgTagsInventory】对象 fg_tags_inventory
 *
 * @author tingming jiang
 * @date 2023-08-01
 */

@Data
public class TagsInventory implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 关联UID */
    @Excel(name = "关联UID")
    private String uid;

    /** 客户PN */
    @Excel(name = "客户PN")
    private String clientPn;

    /** 贴纸数量 */
    @Excel(name = "贴纸数量")
    private Long quantity;

    /**  */
    @Excel(name = "")
    private Long status;

    /** 客户批次号 */
    @Excel(name = "客户批次号")
    private String clientBatch;

    /** 贴纸日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "贴纸日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date productionDate;

    /** 鸿通PN */
    @Excel(name = "鸿通PN")
    private String pn;

    /** 库位 */
    @Excel(name = "库位")
    private String stock;

    /**  */
    @Excel(name = "")
    private String po;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("uid", getUid())
                .append("clientPn", getClientPn())
                .append("quantity", getQuantity())
                .append("status", getStatus())
                .append("clientBatch", getClientBatch())
                .append("productionDate", getProductionDate())
                .append("pn", getPn())
                .append("stock", getStock())
                .append("po", getPo())
                .append("createtime", getCreateTime())
                .toString();
    }
}

