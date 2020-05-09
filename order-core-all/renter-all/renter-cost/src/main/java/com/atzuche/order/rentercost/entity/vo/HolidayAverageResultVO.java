package com.atzuche.order.rentercost.entity.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HolidayAverageResultVO {
	/**
     * 原始租车单价金额
     */
	private Integer rentOriginalUnitPriceAmt;
	/**
     * 实际租金单价
     */
	private Integer actRentUnitPriceAmt;
	/**
     * 抵扣比例
     */
	private Double discounRatio;
	/**
     * 减免金额
     */
	private Integer reductionAmt;
	/**
	 * 还车时间(格式：yyyy-MM-dd HH:mm:ss)
	 */
	private String revertTime;
}
