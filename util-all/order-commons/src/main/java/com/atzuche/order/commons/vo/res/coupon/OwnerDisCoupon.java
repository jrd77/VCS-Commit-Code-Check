package com.atzuche.order.commons.vo.res.coupon;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

public class OwnerDisCoupon implements Serializable {

    private static final long serialVersionUID = 5919430224276251939L;

    /**
     * 优惠券码
     */
    @AutoDocProperty(value="优惠券码")
    private String couponNo;

    /**
     * 优惠券名称 例如:车主优惠券
     */
    @AutoDocProperty(value="优惠券名称")
    private String couponName;

    /**
     * 条件金额
     */
    @AutoDocProperty(value="条件金额")
    private Integer condAmount;

    /**
     * 抵扣金额
     */
    @AutoDocProperty(value="抵扣金额")
    private Integer discount;

    /**
     * 有效开始时间(yyyy.MM.dd HH:mm)
     */
    @AutoDocProperty(value="有效开始时间(yyyy.MM.dd HH:mm)")
    private String validBeginTime;

    /**
     * 有效结束时间(yyyy.MM.dd HH:mm)
     */
    @AutoDocProperty(value="有效结束时间(yyyy.MM.dd HH:mm)")
    private String validEndTime;


    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getCondAmount() {
        return condAmount;
    }

    public void setCondAmount(Integer condAmount) {
        this.condAmount = condAmount;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getValidBeginTime() {
        return validBeginTime;
    }

    public void setValidBeginTime(String validBeginTime) {
        this.validBeginTime = validBeginTime;
    }

    public String getValidEndTime() {
        return validEndTime;
    }

    public void setValidEndTime(String validEndTime) {
        this.validEndTime = validEndTime;
    }
}
