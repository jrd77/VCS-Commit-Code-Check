package com.atzuche.order.commons.enums.cashcode;

/**
 * 收费序号,车主从20开头  车主 第一位：2， 第二位：1正2负 1正2负      1正2负 3可正可负   ->0可正可负，第三位：0，第四位：分模块（区域），后4位是编码
 * @author jing.huang
 *
 */
//@Getter //反馈打了jar调不出来get set方法。
public enum OwnerCashCodeEnum {
	//第一区域块 看第4位，从1开始。  后4位从0001开始
	RENT_AMT("21010001","租金"),
	GPS_SERVICE_AMT("22010002","GPS服务费"),
	HW_DEPOSIT_DEBT("22010003","车载押金"),
	SERVICE_CHARGE("22010004","平台服务费"),
	PROXY_CHARGE("22010005","代管车服务费"),
	OIL_COST_OWNER("20010006","油费"),
	MILEAGE_COST_OWNER("21010007","超里程费用"),
	OWNER_MODIFY_SRV_ADDR_COST("22010008","车主修改取还车地址总费用"),
	SRV_GET_COST_OWNER("22010009","车主取车服务费"),
	SRV_RETURN_COST_OWNER("22010010","车主还车服务费"),
//    OWNER_RENTER_PRICE("22020041","租客车主相互调价"),  //????


    //补贴 //第二区域块 看第4位，从2开始。 后4位从0001开始
    SUBSIDY_OWNER_TORENTER_RENTAMT("22120001", "车主给租客的租金补贴金额"),
    OWNER_MILEAGE_COST_SUBSIDY("22120002","车主超里程补贴"),
    OWNER_OIL_SUBSIDY("22120003","车主油费补贴"),
    OWNER_GOODS_SUBSIDY("22120004","车主物品损失补贴"),
    OWNER_DELAY_SUBSIDY("22120005","车主延时补贴"),
    OWNER_TRAFFIC_SUBSIDY("22120006","车主交通费补贴"),
    OWNER_INCOME_SUBSIDY("22120007","车主收益补贴"),
    OWNER_WASH_CAR_SUBSIDY("22120008","车主洗车补贴"),
    OWNER_OTHER_SUBSIDY("22120009","其他补贴"),
    
    
    
    //第三区域块 看第4位，从3开始。  后4位从0001开始    后面是以此类推，确保费用编码不能重复。
    ACCOUNT_DEBT("21130001","历史欠款"),
    ACCOUNT_OWNER_DEBT("21130002","车主费用"),
    ACCOUNT_OWNER_INCOME("21130003","车主收益"),
    ACCOUNT_WHOLE_RENTER_FINE_COST("21130004","全局订单罚金"),
    ACCOUNT_OWNER_PROXY_EXPENSE_COST("21130005","车主端代管车服务费"),
    ACCOUNT_OWNER_SERVICE_EXPENSE_COST("21130006","车主端平台服务费"),
    ACCOUNT_OWNER_SUBSIDY_COST("21130007","车主补贴明细"),
    ACCOUNT_OWNER_INCREMENT_COST("21130008","车主增值服务费用"),
    ACCOUNT_OWNER_GPS_COST("21130009","gps服务费"),
    ACCOUNT_OWNER_SETTLE_OIL_COST("21130010","车主油费"),
    

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
	
	OwnerCashCodeEnum(String cashNo, String txt) {
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
