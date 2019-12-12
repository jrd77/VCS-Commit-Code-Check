package com.autoyol.platformcost.model;

public class CarDepositAmtVO {

	/**
	 * 车辆押金
	 */
	private Integer carDepositAmt;
	/**
	 * 减免比例
	 */
	private Double reliefPercetage;
	/**
	 * 押金系数
	 */
	private Double carDepositRadio;
	/**
	 * 减免金额
	 */
	private Integer reliefAmt;
	public Integer getCarDepositAmt() {
		return carDepositAmt;
	}
	public void setCarDepositAmt(Integer carDepositAmt) {
		this.carDepositAmt = carDepositAmt;
	}
	public Double getReliefPercetage() {
		return reliefPercetage;
	}
	public void setReliefPercetage(Double reliefPercetage) {
		this.reliefPercetage = reliefPercetage;
	}
	public Double getCarDepositRadio() {
		return carDepositRadio;
	}
	public void setCarDepositRadio(Double carDepositRadio) {
		this.carDepositRadio = carDepositRadio;
	}
	public Integer getReliefAmt() {
		return reliefAmt;
	}
	public void setReliefAmt(Integer reliefAmt) {
		this.reliefAmt = reliefAmt;
	}
	
}
