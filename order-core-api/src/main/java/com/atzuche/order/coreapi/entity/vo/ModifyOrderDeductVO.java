package com.atzuche.order.coreapi.entity.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderDeductVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4991321162323379340L;

	/**
	 * 车主券抵扣金额
	 */
    private Integer ownerCouponOffsetCost;
    
    /**
     * 限时立减
     */
    private Integer reductionAmt;

	/**
     * 平台券抵扣金额
     */
    private Integer discouponAmt;
    
    /**
	 * 取送车优惠券抵扣金额
	 */
	private Integer getCarFeeDiscouponOffsetAmt;

    /**
     * 凹凸币抵扣金额
     */
    private Integer autoCoinDeductibleAmt;
}
