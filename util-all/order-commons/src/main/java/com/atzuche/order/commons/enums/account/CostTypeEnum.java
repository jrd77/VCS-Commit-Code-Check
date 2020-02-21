package com.atzuche.order.commons.enums.account;

import lombok.Getter;

/**
 * 订单结算 费用类型
 */
@Getter
public enum CostTypeEnum {

    RENTER_SUBSIDY(1,"租客补贴"),
    OWNER_SUBSIDY(2,"车主补贴"),
    CONSOLE_SUBSIDY(3,"管理后台补贴"),
    RENTER_FINE(4,"租客罚金"),
    OWNER_FINE(5,"车主罚金"),
    CONSOLE_FINE(6,"平台罚金"),
    RENTER_CONSOLE_COST(7,"管理后台添加租客费用"),
    OWNER_CONSOLE_COST(8,"管理后台添加车主费用"),
    ;

	/**
	 * 订单结算状态
	 */
	private Integer code;

	/**
	 * 订单结算状态
	 */
	private String text;

	CostTypeEnum(Integer code, String text) {
		this.code = code;
		this.text = text;
	}
}
