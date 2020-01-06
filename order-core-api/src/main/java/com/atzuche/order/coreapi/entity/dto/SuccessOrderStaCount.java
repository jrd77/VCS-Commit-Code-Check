package com.atzuche.order.coreapi.entity.dto;

public class SuccessOrderStaCount {

	private Integer successCount;
	private Integer renterCount;
	private Integer illegalCount;
	public Integer getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}
	public Integer getRenterCount() {
		return renterCount;
	}
	public void setRenterCount(Integer renterCount) {
		this.renterCount = renterCount;
	}
	public Integer getIllegalCount() {
		return illegalCount;
	}
	public void setIllegalCount(Integer illegalCount) {
		this.illegalCount = illegalCount;
	}
	
}
