package com.atzuche.order.commons.vo.res.coupon;


import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

public class DisCoupon implements Serializable {

    private static final long serialVersionUID = -6928860708071873441L;

    @AutoDocProperty(value="优惠券id")
    private String disCouponId;

    @AutoDocProperty(value="优惠券标题")
    private String disName;

    @AutoDocProperty(value="满(元)")
    private String condAmt;

    @AutoDocProperty(value="减(元)")
    private String disAmt;

    @AutoDocProperty(value="开始时间(yyyyMMddHHmmss)")
    private String startDate;

    @AutoDocProperty(value="结束时间(yyyyMMddHHmmss)")
    private String endDate;

    @AutoDocProperty(value="实际抵扣金额")
    private String realCouponOffset;


    public String getDisCouponId() {
        return disCouponId;
    }

    public void setDisCouponId(String disCouponId) {
        this.disCouponId = disCouponId;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getCondAmt() {
        return condAmt;
    }

    public void setCondAmt(String condAmt) {
        this.condAmt = condAmt;
    }

    public String getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt = disAmt;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRealCouponOffset() {
        return realCouponOffset;
    }

    public void setRealCouponOffset(String realCouponOffset) {
        this.realCouponOffset = realCouponOffset;
    }
}
