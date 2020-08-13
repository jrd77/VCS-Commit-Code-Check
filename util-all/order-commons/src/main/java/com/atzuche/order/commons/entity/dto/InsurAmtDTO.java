package com.atzuche.order.commons.entity.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InsurAmtDTO {

	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 提前时间（分钟数）
	 */
	private Integer getCarBeforeTime;
	/**
	 * 延后时间（分钟数）
	 */
	private Integer returnCarAfterTime;
	/**
	 * 车辆指导价格
	 */
	private Integer guidPrice;
	/**
	 * 保费计算用购置价（保费购置价为空取车辆指导价算）
	 */
	private Integer inmsrp;
	/**
	 * 驾驶证初次领证日期
	 */
	private LocalDate certificationTime;
	/**
	 * 车辆标签
	 */
	private List<String> carLabelIds;
	/**
	 * 驾驶行为评分
	 */
	private String driverScore;
	/**
	 * 车辆座位数
	 */
	private Integer seatNum;
	/**
	 * 是否购买轮胎保障服务
	 */
	private Integer tyreInsurFlag;
	/**
	 * 是否购买驾乘无忧保障服务
	 */
	private Integer driverInsurFlag;
	/**
	 * 车辆等级
	 */
	private Integer carLevel;
}
