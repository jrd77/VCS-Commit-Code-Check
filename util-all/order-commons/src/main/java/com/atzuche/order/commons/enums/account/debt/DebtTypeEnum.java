package com.atzuche.order.commons.enums.account.debt;

import lombok.Getter;

/**
 * 类型（0订单取消罚金/1订单结算欠款）
 */
@Getter
public enum DebtTypeEnum {

	SETTLE(1,"订单结算欠款"),
    CANCEL(0,"订单取消罚金"),
    ;

	/**
	 * 类型code
	 */
	private Integer code;

	/**
	 * 类型描述
	 */
	private String text;

	DebtTypeEnum(Integer code, String text) {
		this.code = code;
		this.text = text;
	}
}
