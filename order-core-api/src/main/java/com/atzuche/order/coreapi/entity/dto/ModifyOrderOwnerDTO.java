package com.atzuche.order.coreapi.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderOwnerDTO {

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 起租时间
	 */
	private LocalDateTime rentTime;
	/**
	 * 还车时间
	 */
	private LocalDateTime revertTime;
	/**
	 * 取车地址
	 */
	private String getCarAddress;
	/**
	 * 取车地址纬度
	 */
    private String getCarLat;
	/**
	 * 取车地址经度
	 */
    private String getCarLon;
	/**
	 * 还车地址
	 */
	private String revertCarAddress;
	/**
	 * 还车地址纬度
	 */
    private String revertCarLat;
	/**
	 * 还车地址经度
	 */
    private String revertCarLon; 
}
