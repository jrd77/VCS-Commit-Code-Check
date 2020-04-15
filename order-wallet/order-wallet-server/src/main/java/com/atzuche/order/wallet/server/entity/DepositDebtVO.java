package com.atzuche.order.wallet.server.entity;

public class DepositDebtVO {

	private String payKind;
	private String transKind;
	private String transType;
	private Integer transAmt;
	public String getPayKind() {
		return payKind;
	}
	public void setPayKind(String payKind) {
		this.payKind = payKind;
	}
	public String getTransKind() {
		return transKind;
	}
	public void setTransKind(String transKind) {
		this.transKind = transKind;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public Integer getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(Integer transAmt) {
		this.transAmt = transAmt;
	}
	
}
