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
	 * 车牌号
	 */
	private String carPlateNum;
}
