package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 订单券表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:45:08
 * @Description:
 */
@Data
public class OrderCouponDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 租客子订单号
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
