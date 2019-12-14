package com.atzuche.order.coreapi.entity.dto;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 2:12 下午
 **/
public enum CouponType {
    /**
     * 普通优惠券
     */
    NORMAL_COUPON(1),
    /**
     * 取还车优惠券
     */
    GET_AND_RETURN_COUPONT(2),
    /**
     * 车主优惠券
     */
    CAR_OWNER_COUPON(3);

    private int type;

    CouponType(int type) {
        this.type = type;
    }
}
