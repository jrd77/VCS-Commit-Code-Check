package com.autoyol.platformcost.model;

import java.io.Serializable;
public class InsuranceConfig implements Serializable{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3733094243048263347L;
	private Integer guidPriceBegin; 
    private Integer guidPriceEnd;
    private Integer insuranceValue;
	public Integer getGuidPriceBegin() {
		return guidPriceBegin;
	}
	public void setGuidPriceBegin(Integer guidPriceBegin) {
		this.guidPriceBegin = guidPriceBegin;
	}
	public Integer getGuidPriceEnd() {
		return guidPriceEnd;
	}
	public void setGuidPriceEnd(Integer guidPriceEnd) {
		this.guidPriceEnd = guidPriceEnd;
	}
	public Integer getInsuranceValue() {
		return insuranceValue;
	}
	public void setInsuranceValue(Integer insuranceValue) {
		this.insuranceValue = insuranceValue;
	}
    
}
