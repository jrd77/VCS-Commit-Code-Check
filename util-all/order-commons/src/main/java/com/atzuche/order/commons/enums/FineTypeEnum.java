package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum FineTypeEnum {

	MODIFY_GET_FINE(1,"修改订单取车违约金"),
	MODIFY_RETURN_FINE(2,"修改订单还车违约金"),
	MODIFY_ADVANCE(3,"修改订单提前还车违约金"),
	CANCEL_FINE(4,"取消订单违约金")
	;
	
	private Integer fineType;
	private String fineTypeDesc;
	
	FineTypeEnum(Integer fineType, String fineTypeDesc) {
		this.fineType = fineType;
		this.fineTypeDesc = fineTypeDesc;
	}
}
