package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum OwnerFineTypeEnum {

	MODIFY_ADDRESS_FINE(1,"车主修改交接车地址罚金"),
	CANCEL_FINE(4,"取消订单违约金")
	;
	
	private Integer fineType;
	private String fineTypeDesc;
	
	OwnerFineTypeEnum(Integer fineType, String fineTypeDesc) {
		this.fineType = fineType;
		this.fineTypeDesc = fineTypeDesc;
	}
}
