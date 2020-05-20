package com.atzuche.order.commons.entity.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AbatementAmtDTO {
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
     * 是否计算全面保障费
     */
	private Boolean isAbatement;
	/**
	 * 驾驶行为评分
	 */
	private Integer driverScore;
}
