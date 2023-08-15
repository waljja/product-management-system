package com.ktg.mes.fg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktg.common.annotation.Excel;

import java.util.Date;

/**
 * 拆箱明细
 *
 * @author JiangTiangming
 * @date 2023-04-28
 */
public class FgUnpacking {

    private Long id;

    /** 需拆箱的uid */
    @Excel(name = "需拆箱的uid")
    private String uid;

    /** uid_no */
    @Excel(name = "uid_no")
    private long uid_no;

    /** 需求数 */
    @JsonProperty("demandQty")
    @Excel(name = "需求数")
    private long demandQty;

    /** 创建时间 */
    @JsonProperty("CreateTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date CreateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getUid_no() {
        return uid_no;
    }

    public void setUid_no(long uid_no) {
        this.uid_no = uid_no;
    }

    public long getDemandQty() {
        return demandQty;
    }

    public void setDemandQty(long demandQty) {
        this.demandQty = demandQty;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    @Override
    public String toString() {
        return "FgUnpacking{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", uid_no=" + uid_no +
                ", demandQty=" + demandQty +
                ", CreateTime=" + CreateTime +
                '}';
    }
}
