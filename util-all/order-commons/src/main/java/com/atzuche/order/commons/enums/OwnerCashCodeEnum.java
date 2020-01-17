package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 收费序号,车主从20开头  车主 第一位：2， 第二位：1正2负 1正2负      1正2负 3可正可负   ->0可正可负，第三位：0，第四位：分模块，后4位是编码
 * @author jing.huang
 *
 */
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
	SRV_RETURN_COST_OWNER("22060040","车主还车服务费"),
    OWNER_RENTER_PRICE("22060041","租客车主相互调价"),  //????

    OWNER_MILEAGE_COST_SUBSIDY("12120052","车主超里程补贴"),
    OWNER_OIL_SUBSIDY("12120053","车主油费补贴"),
    OWNER_GOODS_SUBSIDY("12120054","车主物品损失补贴"),
    OWNER_DELAY_SUBSIDY("12120055","车主延时补贴"),
    OWNER_TRAFFIC_SUBSIDY("12120056","车主交通费补贴"),
    OWNER_INCOME_SUBSIDY("12120056","车主收益补贴"),
    OWNER_WASH_CAR_SUBSIDY("12120057","车主洗车补贴"),
    //补贴
    SUBSIDY_OWNER_TORENTER_RENTAMT("22120029", "车主给租客的租金补贴金额"),
    
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
