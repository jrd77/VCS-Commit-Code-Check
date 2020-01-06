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

    REAL_COUPON_OFFSET("12120010", "优惠券抵扣金额"),
    GETCARFEE_COUPON_OFFSET("12120013", "取送服务券抵扣金额"),
    OWNER_COUPON_OFFSET_COST("12120051", "车主券抵扣金额"),
    WALLET_DEDUCT("12120011", "钱包抵扣金额"),
    AUTO_COIN_DEDUCT("12120012", "凹凸币抵扣金额"),
    REAL_LIMIT_REDUCTI("12120028", "限时立减抵扣金额"),



    ACCOUNT_RENTER_WZ_DEPOSIT("21110001","违章押金"),
    ACCOUNT_RENTER_DEPOSIT("21110002","车俩押金"),
    ACCOUNT_DEBT("21110003","历史欠款"),
    ACCOUNT_OWNER_DEBT("21110004","车主费用"),
    ACCOUNT_OWNER_INCOME("21110005","车主收益"),
    ACCOUNT_RENTER_RENT_COST("21110006","租车费用"),
    ACCOUNT_RENTER_RENT_COST_REFUND("21110007","租车费用退还"),
    ACCOUNT_RENTER_RENT_COST_AGAIN("21110008","补付租车费用"),
    ACCOUNT_RENTER_DELIVERY_OIL_COST("21110009","交接车-油费"),
    ACCOUNT_RENTER_DELIVERY_MILEAGE_COST("21110010","交接车-获取超里程费用"),
    ACCOUNT_RENTER_SUBSIDY_COST("21110011","租客补贴"),
    ACCOUNT_RENTER_FINE_COST("21110012","租客罚金"),
    ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST("21110013","管理后台补贴"),
    ACCOUNT_WHOLE_RENTER_FINE_COST("21110014","全局订单罚金"),
    ACCOUNT_OWNER_PROXY_EXPENSE_COST("21110015","车主端代管车服务费"),
    ACCOUNT_OWNER_SERVICE_EXPENSE_COST("21110016","车主端平台服务费"),
    ACCOUNT_OWNER_SUBSIDY_COST("21110017","车主补贴明细列表"),
    ACCOUNT_OWNER_INCREMENT_COST("21110018","车主增值服务费用"),
    ACCOUNT_OWNER_GPS_COST("21110019","gps服务费"),
    ACCOUNT_OWNER_SETTLE_OIL_COST("21110020","车主油费"),








    ACCOUNT_WALLET_COST("51110001","个人钱包"),

    OWNER_RENT_INCOME("31110001","租车收益"),
    OWNER_PENALTIES_INCOME("31110002","违约金收益"),

    CASHIER_RENTER_DEPOSIT("41110001","收银台支付车俩押金"),
    CASHIER_RENTER_WZ_DEPOSIT("41110001","收银台支付违章押金"),
    CASHIER_RENTER_COST("41110001","收银台支付租车费用"),
    CASHIER_RENTER_AGAIN_COST("41110001","收银台支付补付租车费用"),

    SETTLE_DEPOSIT_TO_RENT_COST("51110001","车俩押金转租车费用"),
    SETTLE_RENT_COST_TO_HISTORY_AMT("51110002","租车费用转历史欠款"),
    SETTLE_DEPOSIT_TO_HISTORY_AMT("51110003","车俩押金转历史欠款"),

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
