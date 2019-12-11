package com.autoyol.platformcost.model;

public class FeeResult {
	// 单价
	private Integer unitPrice;
	// 单位/份数
	private Double unitCount;
	// 总价
	private Integer totalFee;
	
	public FeeResult() {}
	
	public FeeResult(Integer unitPrice, Double unitCount, Integer totalFee) {
		this.unitPrice = unitPrice;
		this.unitCount = unitCount;
		this.totalFee = totalFee;
	}
	public Integer getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Double getUnitCount() {
		return unitCount;
	}
	public void setUnitCount(Double unitCount) {
		this.unitCount = unitCount;
	}
	public Integer getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}
	
}
