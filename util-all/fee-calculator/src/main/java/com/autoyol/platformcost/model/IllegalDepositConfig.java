package com.autoyol.platformcost.model;

public class IllegalDepositConfig {
	

    private Integer cityCode;

    private Integer leaseMin;

    private Integer leaseMax;

    private Integer depositAmt;

	public Integer getCityCode() {
		return cityCode;
	}

	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}

	public Integer getLeaseMin() {
		return leaseMin;
	}

	public void setLeaseMin(Integer leaseMin) {
		this.leaseMin = leaseMin;
	}

	public Integer getLeaseMax() {
		return leaseMax;
	}

	public void setLeaseMax(Integer leaseMax) {
		this.leaseMax = leaseMax;
	}

	public Integer getDepositAmt() {
		return depositAmt;
	}

	public void setDepositAmt(Integer depositAmt) {
		this.depositAmt = depositAmt;
	}

}