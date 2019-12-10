package com.autoyol.platformcost.model;

import java.io.Serializable;

public class DepositText implements Serializable{

	private static final long serialVersionUID = -4004243389069995175L;
	
    private String depositType; //1，租车押金，2违章押金
    
    private String purchasePriceBegin;//车辆购置价，开始范围(租车押金使用)
   
    private String purchasePriceEnd;//车辆购置价，结束范围(租车押金使用)
    
    private String depositValue;//根据deposit_type，为违约金金额，或者租金预授权限额
    
    private String cityCode;//城市编码
    
    private String multiple;//倍数(租车押金使用)
	
    public String getDepositType() {
		return depositType;
	}
	public void setDepositType(String depositType) {
		this.depositType = depositType;
	}
	public String getPurchasePriceBegin() {
		return purchasePriceBegin;
	}
	public void setPurchasePriceBegin(String purchasePriceBegin) {
		this.purchasePriceBegin = purchasePriceBegin;
	}
	public String getPurchasePriceEnd() {
		return purchasePriceEnd;
	}
	public void setPurchasePriceEnd(String purchasePriceEnd) {
		this.purchasePriceEnd = purchasePriceEnd;
	}
	public String getDepositValue() {
		return depositValue;
	}
	public void setDepositValue(String depositValue) {
		this.depositValue = depositValue;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
}
