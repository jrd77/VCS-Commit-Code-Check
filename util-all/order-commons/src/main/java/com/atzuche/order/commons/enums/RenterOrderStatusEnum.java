package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum RenterOrderStatusEnum {

	NORMAL_STATUS(1,"正常"),
	WAIT_PAY(2,"待支付"),
	WAIT_CONFIRM(3,"待确认"),
	ALREADY_CANCEL(4,"已取消"),
	ALREADY_REFUSE(5,"已拒绝")
	;
	
	private Integer code;
	private String txt;
	
	RenterOrderStatusEnum(Integer code, String txt) {
		this.code = code;
		this.txt = txt;
	}
}
