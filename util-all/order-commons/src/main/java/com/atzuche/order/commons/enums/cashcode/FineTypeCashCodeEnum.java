package com.atzuche.order.commons.enums.cashcode;

import lombok.Getter;

@Getter
public enum FineTypeCashCodeEnum {

	MODIFY_GET_FINE(1,"11110021","修改订单取车违约金"),   //租客取还车违约金
	MODIFY_RETURN_FINE(2,"11110022","修改订单还车违约金"),  //租客取还车违约金
	MODIFY_ADVANCE(3,"11110023","修改订单提前还车违约金"),    //租客提前还车罚金
	CANCEL_FINE(4,"11110024","取消订单违约金"),   //租客违约罚金
	DELAY_FINE(5,"11110025","延迟还车违约金"),   //租客延迟还车罚金

    MODIFY_ADDRESS_FINE(6,"11110026","车主修改交接车地址罚金"),
    RENTER_ADVANCE_RETURN(7,"11110027","租客提前还车罚金"),
    //RENTER_DELAY_RETURN(8"",,"租客延迟还车罚金"),
    OWNER_FINE(9,"11110028","车主违约罚金"),
    GET_RETURN_CAR(10,"11110030","取还车违约金")

	
	/**
	 * 罚金类型：1-修改订单取车违约金，2-修改订单还车违约金，3-修改订单提前还车违约金，4-取消订单违约金
	 * 租客提前还车罚金
		租客延迟还车罚金
		租客违约罚金          不可编辑
		租客取还车违约金   分开记录的，可编辑
	 */
	;
	
	private Integer fineType;
	private String code;
	private String fineTypeDesc;

    FineTypeCashCodeEnum(Integer fineType, String code, String fineTypeDesc) {
        this.fineType = fineType;
        this.code = code;
        this.fineTypeDesc = fineTypeDesc;
    }
}
