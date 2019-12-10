package com.autoyol.platformcost.model;

/**
 * 平台服务费
 */
public class ServiceExpenseInfo {
	
	//平台服务费
	private Integer serviceExpense;
	private Integer serviceMaxValue;
	private Integer serviceMinValue;


	private Integer serviceProportion;
	
	//代管车服务费
	private Integer proxyExpense;
	private Integer proxyProportion;
	
	public Integer getProxyExpense() {
		return proxyExpense;
	}
	public void setProxyExpense(Integer proxyExpense) {
		this.proxyExpense = proxyExpense;
	}
	public Integer getProxyProportion() {
		return proxyProportion;
	}
	public void setProxyProportion(Integer proxyProportion) {
		this.proxyProportion = proxyProportion;
	}
	public Integer getServiceExpense() {
		return serviceExpense;
	}
	public void setServiceExpense(Integer serviceExpense) {
		this.serviceExpense = serviceExpense;
	}
	public Integer getServiceMaxValue() {
		return serviceMaxValue;
	}
	public void setServiceMaxValue(Integer serviceMaxValue) {
		this.serviceMaxValue = serviceMaxValue;
	}
	public Integer getServiceMinValue() {
		return serviceMinValue;
	}
	public void setServiceMinValue(Integer serviceMinValue) {
		this.serviceMinValue = serviceMinValue;
	}
	public Integer getServiceProportion() {
		return serviceProportion;
	}
	public void setServiceProportion(Integer serviceProportion) {
		this.serviceProportion = serviceProportion;
	}
}
