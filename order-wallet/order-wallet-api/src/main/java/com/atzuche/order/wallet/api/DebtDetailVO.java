package com.atzuche.order.wallet.api;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DebtDetailVO {

	/**
	 * 会员历史欠款
	 */
	private Integer historyDebtAmt = 0;
	/**
	 * 会员订单欠款
	 */
	private Integer orderDebtAmt = 0;
	
	/**
	 * 未支付的补付
	 */
	private Integer noPaySupplementAmt = 0;
}
