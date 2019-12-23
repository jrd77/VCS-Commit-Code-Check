package com.atzuche.order.commons.enums.account;

import lombok.Getter;

@Getter
public enum SubsidyEnum {

	RENTER(1,"租客"),
    OWNER(2,"车主"),
    PLATFORM(3,"平台"),
    ;

	/**
	 * 补贴方编码
	 */
	private int code;

	/**
	 * 费用描述
	 */
	private String text;

	SubsidyEnum(int code, String text) {
		this.code = code;
		this.text = text;
	}
}
