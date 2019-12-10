package com.autoyol.platformcost.model;


public class CarGpsRule {
	/**
	 * 规则生效时间,yyyyMMdd，作为规则号
	 */
	private Integer startDate;
	/**
	 * gps编号,跟car_gps字段一样
	 */
	private Integer serialNumber;
	/**
	 * 8,10000，时间段设置，以逗号分隔
	 */
	private String days;
	/**
	 * 5,2 收费金额设置，以逗号分隔
	 */
	private String fees;
	/**
	 * 0待生效,1生效中,2已失效
	 */
	private Integer ruleStatus;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改人
	 */
	private String updateOp;
	
	
	
	@Override
	public String toString() {
		return "CarGpsRule [startDate=" + startDate + ", serialNumber=" + serialNumber + ", days=" + days + ", fees="
				+ fees + ", ruleStatus=" + ruleStatus + ", createOp=" + createOp + ", updateOp=" + updateOp + "]";
	}
	public Integer getStartDate() {
		return startDate;
	}
	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}
	
	public Integer getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getFees() {
		return fees;
	}
	public void setFees(String fees) {
		this.fees = fees;
	}
	public Integer getRuleStatus() {
		return ruleStatus;
	}
	public void setRuleStatus(Integer ruleStatus) {
		this.ruleStatus = ruleStatus;
	}
	public String getCreateOp() {
		return createOp;
	}
	public void setCreateOp(String createOp) {
		this.createOp = createOp;
	}
	public String getUpdateOp() {
		return updateOp;
	}
	public void setUpdateOp(String updateOp) {
		this.updateOp = updateOp;
	}
		  
}
