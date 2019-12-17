package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum RenterCashCodeEnum {

	RENT_AMT("11110003","租金"),
	INSURE_TOTAL_PRICES("11110004","平台保障费"),
	ABATEMENT_INSURE("11110005","全面保障费"),
	FEE("11110006","平台手续费"),
	SRV_GET_COST("11110007","取车费用"),
	SRV_RETURN_COST("11110008","还车费用"),
	MILEAGE_COST_RENTER("11110015","超里程费用"),
	OIL_COST_RENTER("10110016","油费"),
	FINE_AMT("11110026","提前还车违约金"),
	EXTRA_DRIVER_INSURE("11110027","附加驾驶保险金额"),
	GET_RETURN_FINE_AMT("11110029","修改取还车罚金"),
	GET_BLOCKED_RAISE_AMT("11110036","取车运能加价"),
	RETURN_BLOCKED_RAISE_AMT("11110037","还车运能加价"),
	RENTER_PENALTY("11110039","取消订单违约金"),
	;
	
	/**
	 * 费用编码
	 */
	private String cashNo;
	
	/**
	 * 费用描述
	 */
	private String txt;
	
	RenterCashCodeEnum(String cashNo, String txt) {
		this.cashNo = cashNo;
		this.txt = txt;
	}
}
