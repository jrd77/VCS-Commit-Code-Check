package com.atzuche.order.delivery.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransSimpleMode implements Serializable{
	
	private static final long serialVersionUID = 42668260953165537L;
	
	//@AutoDocProperty(value = "订单号")
	private String orderNo;
	//@AutoDocProperty(value = "城市编码")
	private Integer cityCode;
	//@AutoDocProperty(value = "配送模式：0-区间配送，1-精准配送")
	private Integer distributionMode; 
	//@AutoDocProperty(value = "取车时间")
	private LocalDateTime rentTime;
	//@AutoDocProperty(value = "还车时间 ")
	private LocalDateTime revertTime;
	/**
	 * 取车提前时间（使用取车服务时），单位为分钟(车主需要)
	 */
	private Integer getCarBeforeTime;
	/**
	 * 还车延后时间（使用还车服务时），单位为分钟(车主需要)
	 */
	private Integer returnCarAfterTime;
	
	public TransSimpleMode(){}
	
	public TransSimpleMode(String orderNo, Integer cityCode,
			Integer distributionMode, LocalDateTime rentTime, LocalDateTime revertTime,
			Integer getCarBeforeTime, Integer returnCarAfterTime) {
		this.orderNo = orderNo;
		this.cityCode = cityCode;
		this.distributionMode = distributionMode;
		this.rentTime = rentTime;
		this.revertTime = revertTime;
		this.getCarBeforeTime = getCarBeforeTime;
		this.returnCarAfterTime = returnCarAfterTime;
	}
}
