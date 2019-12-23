package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum FineSubsidySourceCodeEnum {

	RENTER("1","租客"),
	OWNER("2","车主"),
	PLATFORM("3","平台")
	;
	
	private String fineSubsidySourceCode;
	private String fineSubsidySourceDesc;
	
	FineSubsidySourceCodeEnum(String fineSubsidySourceCode, String fineSubsidySourceDesc) {
		this.fineSubsidySourceCode = fineSubsidySourceCode;
		this.fineSubsidySourceDesc = fineSubsidySourceDesc;
	}
}
