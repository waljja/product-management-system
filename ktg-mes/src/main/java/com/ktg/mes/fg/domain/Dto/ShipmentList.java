package com.ktg.mes.fg.domain.Dto;

import com.ktg.common.annotation.Excel;

public class ShipmentList {

    /** id */
    @Excel(name = "id")
    private Long id;

    /** 客户 */
    @Excel(name = "客户")
    private String client;

    /** sap_pn */
    @Excel(name = "sap_pn")
    private String sap_pn;

    /** customer_pn */
    @Excel(name = "customer_pn")
    private String customer_pn;

    /** 数量 */
    @Excel(name = "数量")
    private long qty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSap_pn() {
        return sap_pn;
    }

    public void setSap_pn(String sap_pn) {
        this.sap_pn = sap_pn;
    }

    public String getCustomer_pn() {
        return customer_pn;
    }

    public void setCustomer_pn(String customer_pn) {
        this.customer_pn = customer_pn;
    }

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "ShipmentList{" +
                "id=" + id +
                ", client='" + client + '\'' +
                ", sap_pn='" + sap_pn + '\'' +
                ", customer_pn='" + customer_pn + '\'' +
                ", qty=" + qty +
                '}';
    }

    public String toString1() {
        return id +
                "," + client +
                "," + sap_pn +
                "," + customer_pn +
                "," + qty;
    }
}
