package com.atzuche.order.commons.entity.dto;

public class TransProgressDTO {

	private String ownerRentTime;
	private String renterRentTime;
	private String renterRevertTime;
	private String ownerRevertTime;
	public TransProgressDTO(){}
	public TransProgressDTO(String ownerRentTime, String renterRentTime,
			String renterRevertTime, String ownerRevertTime) {
		this.ownerRentTime = ownerRentTime;
		this.renterRentTime = renterRentTime;
		this.renterRevertTime = renterRevertTime;
		this.ownerRevertTime = ownerRevertTime;
	}
	public String getOwnerRentTime() {
		return ownerRentTime;
	}
	public void setOwnerRentTime(String ownerRentTime) {
		this.ownerRentTime = ownerRentTime;
	}
	public String getRenterRentTime() {
		return renterRentTime;
	}
	public void setRenterRentTime(String renterRentTime) {
		this.renterRentTime = renterRentTime;
	}
	public String getRenterRevertTime() {
		return renterRevertTime;
	}
	public void setRenterRevertTime(String renterRevertTime) {
		this.renterRevertTime = renterRevertTime;
	}
	public String getOwnerRevertTime() {
		return ownerRevertTime;
	}
	public void setOwnerRevertTime(String ownerRevertTime) {
		this.ownerRevertTime = ownerRevertTime;
	}
	
	
}
