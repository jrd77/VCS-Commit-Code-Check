package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IllegalDepositAmtDTO {
	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 城市code
	 */
	private Integer cityCode;
	/**
	 * 是否内部员工 1是，其他否
	 */
	private Integer internalStaff;
	/**
	 * 车牌号
	 */
	private String carPlateNum;
}
