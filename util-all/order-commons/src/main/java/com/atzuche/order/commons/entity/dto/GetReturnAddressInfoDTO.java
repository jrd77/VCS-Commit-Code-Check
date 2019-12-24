package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetReturnAddressInfoDTO {
	/**
	 * 基本信息
	 */
	private CostBaseDTO costBaseDTO;
	/**
	 * 取车开关
	 */
	private Integer getFlag;
	/**
	 * 还车开关
	 */
	private Integer returnFlag;
	/**
	 * 取车经度
	 */
	private String getLon;
	/**
	 * 取车纬度
	 */
	private String getLat;
	/**
	 * 还车经度
	 */
	private String returnLon;
	/**
	 * 还车纬度
	 */
	private String returnLat;
	
	public GetReturnAddressInfoDTO() {}
	
	public GetReturnAddressInfoDTO(String getLon, String getLat, String returnLon, String returnLat) {
		this.getLon = getLon;
		this.getLat = getLat;
		this.returnLon = returnLon;
		this.returnLat = returnLat;
	}
	
	public GetReturnAddressInfoDTO(Integer getFlag, Integer returnFlag, String getLon, String getLat, String returnLon,
			String returnLat) {
		this.getFlag = getFlag;
		this.returnFlag = returnFlag;
		this.getLon = getLon;
		this.getLat = getLat;
		this.returnLon = returnLon;
		this.returnLat = returnLat;
	}
}
