package com.atzuche.order.commons.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CancelFineAmtDTO {

	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 取消订单时间
	 */
	private LocalDateTime cancelTime;
	/**
	 * 租金
	 */
	private Integer rentAmt;
	/**
	 * 车辆类型（代管车，托管车，个人车辆等等）
	 */
	private Integer ownerType;
	
}
