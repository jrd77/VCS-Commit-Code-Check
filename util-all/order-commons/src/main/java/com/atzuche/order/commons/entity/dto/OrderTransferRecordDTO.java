package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OrderTransferRecordDTO {
	/**
	 * 订单号
	 */
	@AutoDocProperty(value = "订单号")
	private String orderNo;
	/**
	 * 租客会员号
	 */
	@AutoDocProperty(value = "租客会员号")
	private String memNo;
	/**
	 * 车辆注册号
	 */
	@AutoDocProperty(value = "车辆注册号")
	private String carNo;
	/**
	 * 0:调度换车记录，1:普通换车记录。2:太保换车记录，3:下单车辆记录
	 */
	@AutoDocProperty(value = "0:调度换车记录，1:普通换车记录。2:太保换车记录，3:下单车辆记录")
	private Integer source;
	/**
	 * source对应的描述
	 */
	@AutoDocProperty(value = "source对应的描述")
	private String sourceDesc;
	/**
	 * 操作人
	 */
	@AutoDocProperty(value = "操作人")
	private String operator;
	/**
	 * 车牌号
	 */
	@AutoDocProperty(value = "车牌号")
	private String carPlateNum;
	/**
	 * 换车时间
	 */
	@AutoDocProperty(value = "换车时间")
	private String createTime;
}
