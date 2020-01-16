package com.atzuche.order.commons.enums;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Getter;

/**
 * 收费序号,租客从10开头  租客 第一位：1，第二位：1正2负      1正2负 3可正可负   ->0可正可负，第三位：押金类型，第四位：分模块，后4位是编码
 * @author jing.huang
 *
 */
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
	DISPATCHING_AMT("12130031","升级车辆补贴"),

    REAL_COUPON_OFFSET("12120010", "优惠券抵扣金额"),
    GETCARFEE_COUPON_OFFSET("12120013", "取送服务券抵扣金额"),
    OWNER_COUPON_OFFSET_COST("12120051", "车主券抵扣金额"),
    WALLET_DEDUCT("12120011", "钱包抵扣金额"),
    AUTO_COIN_DEDUCT("12120012", "凹凸币抵扣金额"),
    REAL_LIMIT_REDUCTI("12120028", "限时立减抵扣金额"),

    //补贴费用编码定义  费用编码定义
    SUBSIDY_OWNER_TORENTER_RENTAMT("12120029", "车主给租客的租金补贴金额"),
    //升级车辆补贴
    SUBSIDY_DISPATCHING_AMT("12120030","升级车辆补贴"),
	//油费补贴
    subsidy_oilSubsidy("12120031","油费补贴"),
	//洗车费补贴
	subsidy_cleanCarSubsidy("12120032","洗车费补贴"),
	//取还车迟到补贴
	subsidy_getReturnDelaySubsidy("12120033","取还车迟到补贴"),
	//延时补贴
	subsidy_delaySubsidy("12120034","延时补贴"),
	//交通费补贴
	subsidy_trafficSubsidy("12120035","交通费补贴"),
	//基础保障费补贴
	subsidy_insureSubsidy("12120036","基础保障费补贴"),
	//租金补贴
	subsidy_rentAmtSubsidy("12120037","租金补贴"),
	//其他补贴
	subsidy_otherSubsidy("12120038","其他补贴"),
	//全面保障服务费补贴
	subsidy_abatementSubsidy("12120039","全面保障服务费补贴"),
	//手续费补贴
	subsidy_fee("12120040","手续费补贴"),
	//租客给车主的调价
	subsidy_renterToOwnerAdjust("12120041","租客给车主的调价补贴"),
	//车主给租客的调价
	subsidy_ownerToRenterAdjust("12120042","车主给租客的调价补贴"),
	
    


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
    ACCOUNT_OWNER_SUBSIDY_COST("21110017","车主补贴明细"),
    ACCOUNT_OWNER_INCREMENT_COST("21110018","车主增值服务费用"),
    ACCOUNT_OWNER_GPS_COST("21110019","gps服务费"),
    ACCOUNT_OWNER_SETTLE_OIL_COST("21110020","车主油费"),


    ACCOUNT_RENTER_DETAIN_CAR_AMT("21110021","暂扣的租车费用"),





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
    SETTLE_RENT_COST_TO_RETURN_AMT("51110004","结算租车费用退还"),
    SETTLE_RENT_DEPOSIT_TO_RETURN_AMT("51110005","结算租车押金退还"),
    SETTLE_OWNER_INCOME_TO_HISTORY_AMT("51110006","车主收益转历史欠款"),
    HISTORY_AMT("51110007","历史欠款"),
    SETTLE_RENT_COST_TO_FINE("51110008","租车费用转罚金"),
    SETTLE_RENT_WALLET_COST_TO_FINE("51110009","租车费用钱包转罚金"),
    SETTLE_RENT_DEPOSIT_COST_TO_FINE("51110010","车辆押金转罚金"),
    SETTLE_RENT_WZ_DEPOSIT_COST_TO_FINE("51110011","违章押金转罚金"),
    CANCEL_RENT_DEPOSIT_TO_RETURN_AMT("51110012","取消租车押金退还"),
    CANCEL_RENT_WZ_DEPOSIT_TO_RETURN_AMT("51110013","取消违章押金退还"),
    CANCEL_WZ_DEPOSIT_TO_HISTORY_AMT("51110014","违章押金转历史欠款"),
    CANCEL_RENT_COST_TO_RETURN_AMT("51110015","取消租车费用退还"),

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
