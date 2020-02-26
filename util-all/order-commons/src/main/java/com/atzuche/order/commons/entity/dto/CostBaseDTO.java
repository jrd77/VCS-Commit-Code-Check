package com.atzuche.order.commons.entity.dto;

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
	 * 租客子订单号
	 */
	private String renterOrderNo;
	/**
	 * 车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 开始时间
	 */
	private LocalDateTime startTime;
	/**
	 * 结束时间
	 */
	private LocalDateTime endTime;
	public CostBaseDTO(String orderNo, String renterOrderNo, String memNo, LocalDateTime startTime,
			LocalDateTime endTime) {
		this.orderNo = orderNo;
		this.renterOrderNo = renterOrderNo;
		this.memNo = memNo;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public CostBaseDTO(LocalDateTime startTime, LocalDateTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public CostBaseDTO() {}
	
}
