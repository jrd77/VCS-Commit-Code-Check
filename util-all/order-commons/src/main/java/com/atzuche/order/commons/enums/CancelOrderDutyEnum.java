package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 取消订单判定责任方
 *
 * @author pengcheng.fu
 * @date 2020/1/8 16:32
 */

@Getter
public enum CancelOrderDutyEnum {
    /**
     * 优惠券类型-平台优惠券
     **/
    CANCEL_ORDER_DUTY_RENTER(1, "租客责任"),
    /**
     * 优惠券类型-车主优惠券
     **/
    CANCEL_ORDER_DUTY_OWNER(2, "车主责任"),
    /**
     * 优惠券类型-送取服务券
     **/
    CANCEL_ORDER_DUTY_PLATFORM(6, "双方无责、平台承担保险");


    private int code;

    private String name;

    CancelOrderDutyEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


}
