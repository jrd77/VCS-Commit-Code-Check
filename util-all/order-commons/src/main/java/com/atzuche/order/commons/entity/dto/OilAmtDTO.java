package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OilAmtDTO {
	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 车辆类型（代管车，托管车，个人车辆等等）
	 */
	private Integer carOwnerType;
	/**
	 * 城市编号
	 */
	private Integer cityCode;
	/**
	 * 油箱容量
	 */
	private Integer oilVolume;
	/**
	 * 油品类型
	 */
	private Integer engineType;
	/**
	 * 取车油表刻度
	 */
	private Integer getOilScale;
	/**
	 * 还车油表刻度
	 */
	private Integer returnOilScale;
	/**
	 * 总油表刻度
	 */
	private Integer oilScaleDenominator;
}
