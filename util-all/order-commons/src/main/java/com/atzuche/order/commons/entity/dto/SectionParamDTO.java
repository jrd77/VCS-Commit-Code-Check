package com.atzuche.order.commons.entity.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SectionParamDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7906440019602297753L;

	/**
	 * 起租时间
	 */
	private LocalDateTime rentTime;
	/**
	 * 还车时间
	 */
	private LocalDateTime revertTime;
	/**
	 * 订单开始时间前xx分钟
	 */
	private Integer rentBeforeMinutes;
	/**
	 * 订单开始时间后xx分钟
	 */
	private Integer rentAfterMinutes;
	/**
	 * 订单结束时间前xx分钟
	 */
	private Integer revertBeforeMinutes;
	/**
	 * 订单结束时间后xx分钟
	 */
	private Integer revertAfterMinutes;
	/**
	 * 取车提前时间（使用取车服务时），单位为分钟(车主需要)
	 */
	private Integer getCarBeforeTime;
	/**
	 * 还车延后时间（使用还车服务时），单位为分钟(车主需要)
	 */
	private Integer returnCarAfterTime;
	public LocalDateTime getRentTime() {
		return rentTime;
	}
	public void setRentTime(LocalDateTime rentTime) {
		this.rentTime = rentTime;
	}
	public LocalDateTime getRevertTime() {
		return revertTime;
	}
	public void setRevertTime(LocalDateTime revertTime) {
		this.revertTime = revertTime;
	}
	public Integer getRentBeforeMinutes() {
		return rentBeforeMinutes;
	}
	public void setRentBeforeMinutes(Integer rentBeforeMinutes) {
		this.rentBeforeMinutes = rentBeforeMinutes;
	}
	public Integer getRentAfterMinutes() {
		return rentAfterMinutes;
	}
	public void setRentAfterMinutes(Integer rentAfterMinutes) {
		this.rentAfterMinutes = rentAfterMinutes;
	}
	public Integer getRevertBeforeMinutes() {
		return revertBeforeMinutes;
	}
	public void setRevertBeforeMinutes(Integer revertBeforeMinutes) {
		this.revertBeforeMinutes = revertBeforeMinutes;
	}
	public Integer getRevertAfterMinutes() {
		return revertAfterMinutes;
	}
	public void setRevertAfterMinutes(Integer revertAfterMinutes) {
		this.revertAfterMinutes = revertAfterMinutes;
	}
	public Integer getGetCarBeforeTime() {
		return getCarBeforeTime;
	}
	public void setGetCarBeforeTime(Integer getCarBeforeTime) {
		this.getCarBeforeTime = getCarBeforeTime;
	}
	public Integer getReturnCarAfterTime() {
		return returnCarAfterTime;
	}
	public void setReturnCarAfterTime(Integer returnCarAfterTime) {
		this.returnCarAfterTime = returnCarAfterTime;
	}
	
}
