package com.atzuche.order.commons.entity.dto;

import lombok.Data;

@Data
public class OrderTransferRecordDTO {
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 租客会员号
	 */
	private String memNo;
	/**
	 * 车辆注册号
	 */
	private String carNo;
	/**
	 * 0:调度换车记录，1:普通换车记录。2:太保换车记录，3:下单车辆记录
	 */
	private Integer source;
	/**
	 * source对应的描述
	 */
	private String sourceDesc;
	/**
	 * 操作人
	 */
	private String operator;
	/**
	 * 车牌号
	 */
	private String carPlateNum;
	/**
	 * 换车时间
	 */
	private String createTime;
}
