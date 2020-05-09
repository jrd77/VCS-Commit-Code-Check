package com.atzuche.order.rentercost.entity.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HolidayAverageDateTimeVO {

	/**
	 * 日均价
	 */
	private Integer rentOriginalUnitPriceAmt;
	/**
	 * 还车时间用来分组(格式：yyyy-MM-dd HH:mm:ss)
	 */
	private String revertTime;
}
