package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum FineSubsidyCodeEnum {

	RENTER("1","租客"),
	OWNER("2","车主"),
	PLATFORM("3","平台")
	;
	
	private String fineSubsidyCode;
	private String fineSubsidyDesc;
	
	FineSubsidyCodeEnum(String fineSubsidyCode, String fineSubsidyDesc) {
		this.fineSubsidyCode = fineSubsidyCode;
		this.fineSubsidyDesc = fineSubsidyDesc;
	}
}
