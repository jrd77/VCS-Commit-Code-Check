package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum OwnerCashCodeEnum {
	RENT_AMT("21010035","租金"),
	GPS_SERVICE_AMT("22020003","GPS服务费"),
	HW_DEPOSIT_DEBT("22020004","车载押金"),
	SERVICE_CHARGE("22020005","平台服务费"),
	PROXY_CHARGE("22020006","代管车服务费"),
	OIL_COST_OWNER("20010007","油费"),
	MILEAGE_COST_OWNER("21010009","超里程费用"),
	OWNER_MODIFY_SRV_ADDR_COST("22010027","车主修改取还车地址总费用"),
	SRV_GET_COST_OWNER("22060039","车主取车服务费"),
	SRV_RETURN_COST_OWNER("22060040","车主还车服务费")
	;
	
	/**
	 * 费用编码
	 */
	private String cashNo;
	
	/**
	 * 费用描述
	 */
	private String txt;
	
	OwnerCashCodeEnum(String cashNo, String txt) {
		this.cashNo = cashNo;
		this.txt = txt;
	}
}
