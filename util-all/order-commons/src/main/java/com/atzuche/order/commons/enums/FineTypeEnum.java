package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum FineTypeEnum {

	MODIFY_GET_FINE(1,"修改订单取车违约金"),   //租客取还车违约金 
	MODIFY_RETURN_FINE(2,"修改订单还车违约金"),  //租客取还车违约金 
	MODIFY_ADVANCE(3,"修改订单提前还车违约金"),    //租客提前还车罚金
	CANCEL_FINE(4,"取消订单违约金"),   //租客违约罚金
	DELAY_FINE(5,"延迟还车违约金"),   //租客延迟还车罚金
	
	/**
	 * 罚金类型：1-修改订单取车违约金，2-修改订单还车违约金，3-修改订单提前还车违约金，4-取消订单违约金
	 * 租客提前还车罚金
		租客延迟还车罚金
		租客违约罚金          不可编辑
		租客取还车违约金   分开记录的，可编辑
	 */
	;
	
	private Integer fineType;
	private String fineTypeDesc;
	
	FineTypeEnum(Integer fineType, String fineTypeDesc) {
		this.fineType = fineType;
		this.fineTypeDesc = fineTypeDesc;
	}
}
