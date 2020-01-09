package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 * 取消订单返回信息
 *
 * @author pengcheng.fu
 * @date 2020/1/7 17:17
 */

@Data
public class CancelOrderResDTO {

    /**
     * 是否通知退款
     */
    private Boolean isRefund;

    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 是否退还优惠券(平台+送取服务)
     */
    private Boolean isReturnDisCoupon;

    /**
     * 是否退还车主券
     */
    private Boolean isReturnOwnerCoupon;

    /**
     * 车主券编码
     */
    private String ownerCouponNo;

    /**
     * 租车费用支付状态
     */
    private Integer rentCarPayStatus;

    /**
     * 车辆注册号
     */
    private Integer carNo;

}
