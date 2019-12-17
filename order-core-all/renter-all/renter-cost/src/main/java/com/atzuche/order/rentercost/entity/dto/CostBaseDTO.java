package com.atzuche.order.rentercost.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class CostBaseDTO {

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
	/**
	 * 开始时间
	 */
	private LocalDateTime startTime;
	/**
	 * 结束时间
	 */
	private LocalDateTime endTime;
}
