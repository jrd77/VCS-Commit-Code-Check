package com.atzuche.order.coreapi.entity.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OwnerGpsDeductVO {
	/**
	 * GPS押金文案 如GPS押金
	 */
    private String gpsDepositTxt;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 押金金额 如1000
     */
    private String deposit;
    /**
     * 是否显示进入订单详情页箭头 0:否 1:是
     */
    private String isEnterTransFlag;
    /**
     * GPS押金结算日期 如2016.03.15
     */
    private String settleDate;
}
