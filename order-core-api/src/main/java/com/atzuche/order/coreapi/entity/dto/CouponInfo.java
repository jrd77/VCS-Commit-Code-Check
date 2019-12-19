package com.atzuche.order.coreapi.entity.dto;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 2:12 下午
 **/
public class CouponInfo {
    private CouponType couponType;
    private String couponId;

    public CouponType getCouponType() {
        return couponType;
    }

    public void setCouponType(CouponType couponType) {
        this.couponType = couponType;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    @Override
    public String toString() {
        return "CouponInfo{" +
                "couponType=" + couponType +
                ", couponCode='" + couponId + '\'' +
                '}';
    }
}
