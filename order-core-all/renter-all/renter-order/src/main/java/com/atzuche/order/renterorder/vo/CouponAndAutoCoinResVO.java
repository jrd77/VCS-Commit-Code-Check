package com.atzuche.order.renterorder.vo;

import lombok.Data;

/**
 * 优惠券、凹凸币返回信息
 *
 * @author pengcheng.fu
 * @date 2020/1/1 17:15
 */

@Data
public class CouponAndAutoCoinResVO {

    /**
     * 订单是否使用平台优惠券(真实抵扣)
     */
    private Boolean isUsePlatformCoupon;

    /**
     * 订单是否使用送取服务惠券(真实抵扣)
     */
    private Boolean isUseGetCarFeeCoupon;

    /**
     * 订单是否使用车主优惠券(真实抵扣)
     */
    private Boolean isUseOwnerCoupon;

    /**
     * 订单是否使用抵扣凹凸币个数(真实抵扣)
     */
    private Integer chargeAutoCoin;

    /**
     * 订单总租金(原始值)
     */
    private Integer rentAmt;

}
