package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

public class SectionProposaltimeDTO {

	@AutoDocProperty(value = "订单号")
	private String orderNo;
	@AutoDocProperty(value = "租客处建议送达时间，格式：yyyy-MM-dd HH:mm:ss")
	private String renterProposalGetTime;
	@AutoDocProperty(value = "租客处建议取车时间，格式：yyyy-MM-dd HH:mm:ss")
	private String renterProposalReturnTime;
	@AutoDocProperty(value = "车主处建议取车时间 ，格式：yyyy-MM-dd HH:mm:ss")
	private String ownerProposalGetTime;
	@AutoDocProperty(value = "车主处建议送达时间 ，格式：yyyy-MM-dd HH:mm:ss")
	private String ownerProposalReturnTime;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getRenterProposalGetTime() {
		return renterProposalGetTime;
	}
	public void setRenterProposalGetTime(String renterProposalGetTime) {
		this.renterProposalGetTime = renterProposalGetTime;
	}
	public String getRenterProposalReturnTime() {
		return renterProposalReturnTime;
	}
	public void setRenterProposalReturnTime(String renterProposalReturnTime) {
		this.renterProposalReturnTime = renterProposalReturnTime;
	}
	public String getOwnerProposalGetTime() {
		return ownerProposalGetTime;
	}
	public void setOwnerProposalGetTime(String ownerProposalGetTime) {
		this.ownerProposalGetTime = ownerProposalGetTime;
	}
	public String getOwnerProposalReturnTime() {
		return ownerProposalReturnTime;
	}
	public void setOwnerProposalReturnTime(String ownerProposalReturnTime) {
		this.ownerProposalReturnTime = ownerProposalReturnTime;
	}
	@Override
	public String toString() {
		return "SectionProposaltimeDTO [orderNo=" + orderNo + ", renterProposalGetTime=" + renterProposalGetTime
				+ ", renterProposalReturnTime=" + renterProposalReturnTime + ", ownerProposalGetTime="
				+ ownerProposalGetTime + ", ownerProposalReturnTime=" + ownerProposalReturnTime + "]";
	}
	
}
