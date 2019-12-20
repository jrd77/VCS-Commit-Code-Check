package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MileageAmtDTO {
	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 车辆类型（代管车，托管车，个人车辆等等）
	 */
	private Integer carOwnerType;
	/**
	 * 日均限里程数
	 */
	private Integer dayMileage;
	/**
	 * 车辆日租金指导价
	 */
	private Integer guideDayPrice;
	/**
	 * 取车里程数
	 */
	private Integer getmileage;
	/**
	 * 还车里程数
	 */
	private Integer returnMileage;
}
