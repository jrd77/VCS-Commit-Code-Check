package com.atzuche.order.parentorder.entity;

import java.util.Date;

public class OrderStopFreightInfo {
    private Integer id;

    private String orderNo;

    private Integer agreementStopFreightRate;

    private Integer notagreementStopFreightRate;

    private Integer agreementStopFreightPrice;

    private Integer notagreementStopFreightPrice;

    private Date createTime;

    private String createOp;

    private Date updateTime;

    private String updateOp;

    private Integer isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getAgreementStopFreightRate() {
        return agreementStopFreightRate;
    }

    public void setAgreementStopFreightRate(Integer agreementStopFreightRate) {
        this.agreementStopFreightRate = agreementStopFreightRate;
    }

    public Integer getNotagreementStopFreightRate() {
        return notagreementStopFreightRate;
    }

    public void setNotagreementStopFreightRate(Integer notagreementStopFreightRate) {
        this.notagreementStopFreightRate = notagreementStopFreightRate;
    }

    public Integer getAgreementStopFreightPrice() {
        return agreementStopFreightPrice;
    }

    public void setAgreementStopFreightPrice(Integer agreementStopFreightPrice) {
        this.agreementStopFreightPrice = agreementStopFreightPrice;
    }

    public Integer getNotagreementStopFreightPrice() {
        return notagreementStopFreightPrice;
    }

    public void setNotagreementStopFreightPrice(Integer notagreementStopFreightPrice) {
        this.notagreementStopFreightPrice = notagreementStopFreightPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp == null ? null : createOp.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateOp() {
        return updateOp;
    }

    public void setUpdateOp(String updateOp) {
        this.updateOp = updateOp == null ? null : updateOp.trim();
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}