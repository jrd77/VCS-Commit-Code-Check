package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 优惠券类型
 *
 * @author pengcheng.fu
 * @date 2019/12/25 16:25
 */
@Getter
public enum CouponTypeEnum {

    /**
     * 优惠券类型-平台优惠券
     **/
    ORDER_COUPON_TYPE_PLATFORM(0, "平台优惠券"),
    /**
     * 优惠券类型-车主优惠券
     **/
    ORDER_COUPON_TYPE_OWNER(1, "车主优惠券"),
    /**
     * 优惠券类型-送取服务券
     **/
    ORDER_COUPON_TYPE_GET_RETURN_SRV(2, "送取服务券");


    private int code;

    private String name;

    CouponTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
