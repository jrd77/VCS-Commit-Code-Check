package com.autoyol.platformcost.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InsuranceCommonDTO {
	// 取车时间
	private LocalDateTime rentTime;
	// 还车时间
	private LocalDateTime revertTime;
	// 取车提前时间（分钟数）
	private Integer getCarBeforeTime;
	// 还车延后时间（分钟数）
	private Integer returnCarAfterTime;
	// 基准小时数
	private Integer configHours;
	// 保费计算用购置价
	private Integer guidPrice;
	// 会员系数
	private Double coefficient;
	// 车辆标签系数
	private Double easyCoefficient;
	// 驾驶行为系数
	private Double driverCoefficient;
	
	public InsuranceCommonDTO() {}

	public InsuranceCommonDTO(LocalDateTime rentTime, LocalDateTime revertTime, Integer getCarBeforeTime,
			Integer returnCarAfterTime, Integer configHours, Integer guidPrice, Double coefficient,
			Double easyCoefficient, Double driverCoefficient) {
		this.rentTime = rentTime;
		this.revertTime = revertTime;
		this.getCarBeforeTime = getCarBeforeTime;
		this.returnCarAfterTime = returnCarAfterTime;
		this.configHours = configHours;
		this.guidPrice = guidPrice;
		this.coefficient = coefficient;
		this.easyCoefficient = easyCoefficient;
		this.driverCoefficient = driverCoefficient;
	}
	
	
}
