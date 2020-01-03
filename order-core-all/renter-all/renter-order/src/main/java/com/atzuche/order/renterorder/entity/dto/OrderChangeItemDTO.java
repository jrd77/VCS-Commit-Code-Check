package com.atzuche.order.renterorder.entity.dto;

public class OrderChangeItemDTO {

	private String orderNo;
	private String renterOrderNo;
	private String changeCode;
	private String changeName;
	
	public OrderChangeItemDTO() {}
	
	public OrderChangeItemDTO(String orderNo, String renterOrderNo, String changeCode, String changeName) {
		this.orderNo = orderNo;
		this.renterOrderNo = renterOrderNo;
		this.changeCode = changeCode;
		this.changeName = changeName;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getRenterOrderNo() {
		return renterOrderNo;
	}
	public void setRenterOrderNo(String renterOrderNo) {
		this.renterOrderNo = renterOrderNo;
	}
	public String getChangeCode() {
		return changeCode;
	}
	public void setChangeCode(String changeCode) {
		this.changeCode = changeCode;
	}
	public String getChangeName() {
		return changeName;
	}
	public void setChangeName(String changeName) {
		this.changeName = changeName;
	}
	
}
