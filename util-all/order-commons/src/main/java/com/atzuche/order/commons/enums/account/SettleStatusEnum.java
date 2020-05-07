package com.atzuche.order.commons.enums.account;

import lombok.Getter;

/**
 * 订单结算状态
 * 结算状态0 未结算 1 已结算
 */
@Getter
public enum SettleStatusEnum {
    SETTL_WALLET_DEDUCT(3,"结算失败,钱包已扣"),
    SETTL_FAIL(2,"结算失败"),
	SETTLED(1,"已结算"),
    SETTLEING(0,"未结算"),
    ;

	/**
	 * 订单结算状态
	 */
	private Integer code;

	/**
	 * 订单结算状态
	 */
	private String text;

	SettleStatusEnum(Integer code, String text) {
		this.code = code;
		this.text = text;
	}
}
