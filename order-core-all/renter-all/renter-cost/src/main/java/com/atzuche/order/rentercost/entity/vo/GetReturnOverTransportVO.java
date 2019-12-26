package com.atzuche.order.rentercost.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class GetReturnOverTransportVO {

	/**
	 *	 取车时间是否超出运能 :true-超出运能，false-未超出
	 */
	private Boolean isGetOverTransport;
	/**
	 *	 取车时间超出运能附加费用
	 */
	private Integer getOverTransportFee;
	
	/**
	 *	 取车时间超出运能附加费用(夜间)
	 */
	private Integer nightGetOverTransportFee;
	
	
	
	/**
	 * 	还车时间是否超出运能:true-超出运能，false-未超出
	 */
	private Boolean isReturnOverTransport;
	/**
	 * 	还车时间超出运能附加费用
	 */
	private Integer returnOverTransportFee;
	
	/**
	 * 	还车时间超出运能附加费用(夜间)
	 */
	private Integer nightReturnOverTransportFee;
	
	/**
	 * 是否变更订单起租时间(修改订单)
	 */
	private Boolean isUpdateRentTime;
	
	/**
	 * 是否变更订单还车时间(修改订单)
	 */
	private Boolean isUpdateRevertTime;
	
	
	public GetReturnOverTransportVO() {
		
	}
	
	public GetReturnOverTransportVO(Boolean isGetOverTransport, Integer getOverTransportFee, Boolean isReturnOverTransport, Integer returnOverTransportFee) {
		this.isGetOverTransport = isGetOverTransport;
		this.getOverTransportFee = getOverTransportFee;
		this.isReturnOverTransport = isReturnOverTransport;
		this.returnOverTransportFee = returnOverTransportFee;
	}
	
	public GetReturnOverTransportVO(Boolean isGetOverTransport, Integer getOverTransportFee,
                                    Integer nightGetOverTransportFee, Boolean isReturnOverTransport, Integer returnOverTransportFee,
                                    Integer nightReturnOverTransportFee) {
		super();
		this.isGetOverTransport = isGetOverTransport;
		this.getOverTransportFee = getOverTransportFee;
		this.nightGetOverTransportFee = nightGetOverTransportFee;
		this.isReturnOverTransport = isReturnOverTransport;
		this.returnOverTransportFee = returnOverTransportFee;
		this.nightReturnOverTransportFee = nightReturnOverTransportFee;
	}

	public GetReturnOverTransportVO(Boolean isGetOverTransport, Integer getOverTransportFee,
                                    Integer nightGetOverTransportFee, Boolean isReturnOverTransport, Integer returnOverTransportFee,
                                    Integer nightReturnOverTransportFee, Boolean isUpdateRentTime, Boolean isUpdateRevertTime) {
		super();
		this.isGetOverTransport = isGetOverTransport;
		this.getOverTransportFee = getOverTransportFee;
		this.nightGetOverTransportFee = nightGetOverTransportFee;
		this.isReturnOverTransport = isReturnOverTransport;
		this.returnOverTransportFee = returnOverTransportFee;
		this.nightReturnOverTransportFee = nightReturnOverTransportFee;
		this.isUpdateRentTime = isUpdateRentTime;
		this.isUpdateRevertTime = isUpdateRevertTime;
	}
	
	

	public Boolean getIsGetOverTransport() {
		return isGetOverTransport;
	}
	public void setIsGetOverTransport(Boolean isGetOverTransport) {
		this.isGetOverTransport = isGetOverTransport;
	}
	public Integer getGetOverTransportFee() {
		return getOverTransportFee;
	}
	public void setGetOverTransportFee(Integer getOverTransportFee) {
		this.getOverTransportFee = getOverTransportFee;
	}
	public Boolean getIsReturnOverTransport() {
		return isReturnOverTransport;
	}
	public void setIsReturnOverTransport(Boolean isReturnOverTransport) {
		this.isReturnOverTransport = isReturnOverTransport;
	}
	public Integer getReturnOverTransportFee() {
		return returnOverTransportFee;
	}
	public void setReturnOverTransportFee(Integer returnOverTransportFee) {
		this.returnOverTransportFee = returnOverTransportFee;
	}
	public Integer getNightGetOverTransportFee() {
		return nightGetOverTransportFee;
	}
	public void setNightGetOverTransportFee(Integer nightGetOverTransportFee) {
		this.nightGetOverTransportFee = nightGetOverTransportFee;
	}
	public Integer getNightReturnOverTransportFee() {
		return nightReturnOverTransportFee;
	}
	public void setNightReturnOverTransportFee(Integer nightReturnOverTransportFee) {
		this.nightReturnOverTransportFee = nightReturnOverTransportFee;
	}
	
	public Boolean getIsUpdateRentTime() {
		return isUpdateRentTime;
	}

	public void setIsUpdateRentTime(Boolean isUpdateRentTime) {
		this.isUpdateRentTime = isUpdateRentTime;
	}

	public Boolean getIsUpdateRevertTime() {
		return isUpdateRevertTime;
	}

	public void setIsUpdateRevertTime(Boolean isUpdateRevertTime) {
		this.isUpdateRevertTime = isUpdateRevertTime;
	}
	
	@Override
	public String toString() {
		return "GetReturnOverTransport [isGetOverTransport=" + isGetOverTransport + ", getOverTransportFee="
				+ getOverTransportFee + ", nightGetOverTransportFee=" + nightGetOverTransportFee
				+ ", isReturnOverTransport=" + isReturnOverTransport + ", returnOverTransportFee="
				+ returnOverTransportFee + ", nightReturnOverTransportFee=" + nightReturnOverTransportFee
				+ ", isUpdateRentTime=" + isUpdateRentTime + ", isUpdateRevertTime=" + isUpdateRevertTime + "]";
	}

	

	
	
	
}
