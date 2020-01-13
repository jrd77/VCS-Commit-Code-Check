package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyConfirmDTO {

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 租客订单号
	 */
	private String renterOrderNo;
	/**
	 * 车主订单号
	 */
	private String ownerOrderNo;
	/**
	 * 车主会员号
	 */
	private String ownerMemNo;
	/**
	 * 操作类型：1-同意，2-拒绝
	 */
	private String flag;
}
