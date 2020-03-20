package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

@Getter
public enum PayLineEnum {
	ON_LINE_PAY(0,"线上支付"),
	OFF_LINE_PAY(1,"线下支付"),
	VIRTUAL_PAY(2,"虚拟支付");
	
	private Integer code;
	private String txt;
	
	PayLineEnum(Integer code, String txt) {
		this.code = code;
		this.txt = txt;
	}
}
