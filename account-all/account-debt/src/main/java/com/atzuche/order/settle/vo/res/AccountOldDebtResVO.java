package com.atzuche.order.settle.vo.res;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountOldDebtResVO {

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 租客子单号
	 */
	private String renterOrderNo;
	/**
	 * 车主子单号
	 */
	private String ownerOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 真实抵扣欠款金额
	 */
	private Integer realDebtAmt;
	/**
	 * 费用编码枚举
	 */
	private RenterCashCodeEnum cahsCodeEnum; 
}
