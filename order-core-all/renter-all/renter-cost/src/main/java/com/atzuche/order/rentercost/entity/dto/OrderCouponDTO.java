package com.atzuche.order.rentercost.entity.dto;


import lombok.Data;

/**
 * 订单优惠券DTO
 *
 * @author pengcheng.fu
 * @date 2019/12/25 15:43
 */

@Data
public class OrderCouponDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;

    /**
     * 优惠券id
     */
    private String couponId;
    /**
     * 优惠券名称
     */
    private String couponName;
    /**
     * 优惠券类型
     */
    private Integer couponType;
    /**
     * 状态 0：未使用 1：已使用
     */
    private Integer status;
    /**
     * 描述信息
     */
    private String couponDesc;
    /**
     * 抵扣的金额
     */
    private Integer amount;
}
