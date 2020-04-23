package com.atzuche.order.commons.entity.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2020-04-07 11:18:55
 * @Description:
 */
@Data
public class OwnerCouponLongDTO implements Serializable {
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
	 * 租客会员号
	 */
	private String renterMemNo;
	/**
	 * 原始租金单价
	 */
	private Integer originalUnitPrice;
	/**
	 * 券编码
	 */
	private String couponCode;
	/**
	 * 实际租金单价
	 */
	private Integer actUnitPrice;
	/**
	 * 单价减免金额
	 */
	private Integer reductionPrice;
	/**
	 * 折扣比例
	 */
	private Double discounRatio;
    /**
     * 折扣文案描述
     */
    private String discountDesc;



}
