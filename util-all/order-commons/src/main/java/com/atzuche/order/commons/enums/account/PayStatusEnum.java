package com.atzuche.order.commons.enums.account;

import lombok.Getter;

/**
 * 支付状态
 */
@Getter
public enum PayStatusEnum {

	PAYED(1,"已支付"),
    UN_PAY(2,"未支付"),
    ;

	/**
	 * 支付状态code
	 */
	private Integer code;

	/**
	 * 支付状态描述
	 */
	private String text;

	PayStatusEnum(Integer code, String text) {
		this.code = code;
		this.text = text;
	}
}
