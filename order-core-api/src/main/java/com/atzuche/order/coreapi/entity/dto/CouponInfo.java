package com.atzuche.order.coreapi.entity.dto;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 2:12 下午
 **/
public class CouponInfo {
    private CouponType couponType;
    private String couponCode;

    public CouponType getCouponType() {
        return couponType;
    }

    public void setCouponType(CouponType couponType) {
        this.couponType = couponType;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    @Override
    public String toString() {
        return "CouponInfo{" +
                "couponType=" + couponType +
                ", couponCode='" + couponCode + '\'' +
                '}';
    }
}
