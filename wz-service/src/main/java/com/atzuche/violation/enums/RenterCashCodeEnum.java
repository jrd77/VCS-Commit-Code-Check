package com.atzuche.violation.enums;

/**
 * 收费序号,租客从10开头  租客 第一位：1，第二位：1正2负      1正2负 3可正可负   ->0可正可负，第三位：押金类型，第四位：分模块（区域），后4位是编码
 * @author jing.huang
 *
 */
//@Getter  //反馈打了jar调不出来get set方法。
public enum RenterCashCodeEnum {
	
	//第一区域块 看第4位，从1开始。  后4位从0001开始
	RENT_AMT("11110001","租金"),
	INSURE_TOTAL_PRICES("11110002","平台保障费"),
	ABATEMENT_INSURE("11110003","全面保障费"),
	FEE("11110004","平台手续费"),
	SRV_GET_COST("11110005","取车费用"),
	SRV_RETURN_COST("11110006","还车费用"),
	MILEAGE_COST_RENTER("11110007","超里程费用"),
	OIL_COST_RENTER("10110008","油费"),
	FINE_AMT("11110009","提前还车违约金"),
	EXTRA_DRIVER_INSURE("11110010","附加驾驶保险金额"),
	GET_RETURN_FINE_AMT("11110011","修改取还车罚金"),
	GET_BLOCKED_RAISE_AMT("11110012","取车运能加价"),
	RETURN_BLOCKED_RAISE_AMT("11110013","还车运能加价"),
	RENTER_PENALTY("11110014","取消订单违约金"),
	DISPATCHING_AMT("12110015","升级车辆补贴"),
    REAL_COUPON_OFFSET("12110016", "优惠券抵扣金额"),
    GETCARFEE_COUPON_OFFSET("12110017", "取送服务券抵扣金额"),
    OWNER_COUPON_OFFSET_COST("12110018", "车主券抵扣金额"),
    WALLET_DEDUCT("12110019", "钱包抵扣金额"),
    AUTO_COIN_DEDUCT("12110020", "凹凸币抵扣金额"),
    REAL_LIMIT_REDUCTI("12110021", "限时立减抵扣金额"),

    
    //	//第二区域块 看第4位，从2开始。  后4位从0001开始
    //补贴费用编码定义  费用编码定义
    SUBSIDY_OWNER_TORENTER_RENTAMT("12120001", "车主给租客的租金补贴金额"),
    //升级车辆补贴
    SUBSIDY_DISPATCHING_AMT("12120002","升级车辆补贴"),
	//油费补贴
    SUBSIDY_OIL("12120003","油费补贴"),
	//洗车费补贴
	SUBSIDY_CLEANCAR("12120004","洗车费补贴"),
	//取还车迟到补贴
	SUBSIDY_GETRETURNDELAY("12120005","取还车迟到补贴"),
	//延时补贴
	SUBSIDY_DELAY("12120006","延时补贴"),
	//交通费补贴
	SUBSIDY_TRAFFIC("12120007","交通费补贴"),
	//基础保障费补贴
	SUBSIDY_INSURE("12120008","基础保障费补贴"),
	//租金补贴
	SUBSIDY_RENTAMT("12120009","租金补贴"),
	//其他补贴
	SUBSIDY_OTHER("12120010","其他补贴"),
	//全面保障服务费补贴
	SUBSIDY_ABATEMENT("12120011","全面保障服务费补贴"),
	//手续费补贴
	SUBSIDY_FEE("12120012","手续费补贴"),
	//租客给车主的调价
	SUBSIDY_RENTERTOOWNER_ADJUST("12120013","租客给车主的调价补贴"),
	//车主给租客的调价
	SUBSIDY_OWNERTORENTER_ADJUST("12120014","车主给租客的调价补贴"),
	
    
	//	第三区域块 看第4位，从3开始。  后4位从0001开始
    ACCOUNT_WALLET_COST("11130001","个人钱包"),
    CASHIER_RENTER_COST("11130002","租车费用"),
    SETTLE_DEPOSIT_TO_RENT_COST("11130003","车辆押金转租车费用"),
    SETTLE_RENT_COST_TO_HISTORY_AMT("11130004","租车费用转历史欠款"),
    SETTLE_DEPOSIT_TO_HISTORY_AMT("11130005","车辆押金转历史欠款"),
    SETTLE_RENT_COST_TO_RETURN_AMT("11130006","结算租车费用退还"),
    SETTLE_RENT_DEPOSIT_TO_RETURN_AMT("11130007","结算租车押金退还"),
    SETTLE_WZ_DEPOSIT_TO_RETURN_AMT("11130008","结算违章押金退还"),
    SETTLE_OWNER_INCOME_TO_HISTORY_AMT("21130008","车主收益转历史欠款"),
    HISTORY_AMT("11130009","历史欠款"),
    SETTLE_RENT_COST_TO_FINE("11130010","租车费用转罚金"),
    SETTLE_RENT_WALLET_COST_TO_FINE("11130011","租车费用钱包转罚金"),
    SETTLE_RENT_DEPOSIT_COST_TO_FINE("11130012","车辆押金转罚金"),
    SETTLE_RENT_WZ_DEPOSIT_COST_TO_FINE("11130013","违章押金转罚金"),
    CANCEL_RENT_DEPOSIT_TO_RETURN_AMT("11130014","取消租车押金退还"),
    CANCEL_RENT_WZ_DEPOSIT_TO_RETURN_AMT("11130015","取消违章押金退还"),
    CANCEL_WZ_DEPOSIT_TO_HISTORY_AMT("11130016","违章押金转历史欠款"),
    CANCEL_RENT_COST_TO_RETURN_AMT("11130017","取消租车费用退还"),
    ACCOUNT_RENTER_DETAIN_CAR_AMT("11130018","暂扣租车费用"),
    ///// 暂扣的考虑以下的费用编码。
    ACCOUNT_RENTER_RENT_COST("11130019","租车费用"),
    ACCOUNT_RENTER_WZ_DEPOSIT("11130020","违章押金"),
    ACCOUNT_RENTER_DEPOSIT("11130021","车辆押金"),
    SETTLE_RENT_WALLET_TO_HISTORY_AMT("11130022","租车钱包费用转历史欠款"),
    SETTLE_WZ_TO_HISTORY_AMT("11130023","违章押金转历史欠款"),

    ACCOUNT_DEPOSIT_DETAIN_CAR_AMT("11130024","暂扣租车押金"),
    ACCOUNT_WZ_DEPOSIT_DETAIN_CAR_AMT("11130025","暂扣违章押金"),

    CANCEL_ACCOUNT_DEPOSIT_DETAIN_CAR_AMT("11130026","取消暂扣租车押金"),
    CANCEL_ACCOUNT_WZ_DEPOSIT_DETAIN_CAR_AMT("11130027","取消暂扣违章押金"),
    SETTLE_WZ_DEPOSIT_TO_WZ_COST("11130028","违章押金转违章费用"),
    

    ACCOUNT_RENTER_RENT_COST_AGAIN("21130022","补付租车费用"),
    ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST("21130023","管理后台补贴"),
    ACCOUNT_RENTER_SUBSIDY_COST("21130024","租客补贴"),
    ACCOUNT_RENTER_RENT_COST_REFUND("21130025","租车费用退还"),
    ACCOUNT_RENTER_DELIVERY_OIL_COST("21130026","交接车-油费"),
    ACCOUNT_RENTER_DELIVERY_MILEAGE_COST("21130027","交接车-获取超里程费用"),
    ACCOUNT_RENTER_FINE_COST("21130028","租客罚金"),
//    ACCOUNT_RENTER_DELIVERY_MILEAGE_COST_PROXY("21130029","交接车-获取代管车超里程费用"),
    
  //第四区域块 看第4位，从4开始。  后4位从0001开始    后面是以此类推，确保费用编码不能重复。
    
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

	public String getCashNo() {
		return cashNo;
	}

	public String getTxt() {
		return txt;
	}
	
	
}
