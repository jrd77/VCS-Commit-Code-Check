package com.autoyol.platformcost.model;

import java.util.List;

public class AbatementConfig {
	/**
	 * 指导价开始值
	 */
	private Integer guidPriceBegin;
	/**
	 * 指导价结束值
	 */
    private Integer guidPriceEnd;
    /**
     * 7天内（含）单价
     */
    private Integer abatementUnitPrice7;
    /**
     * 7天-15天内（含）单价
     */
    private Integer abatementUnitPrice15;
    /**
     *  157天-25天内（含）单价
     */
    private Integer abatementUnitPrice25;
    /**
     * 25天以后单价
     */
    private Integer abatementUnitPriceOther;
    
    
	public AbatementConfig() {}
	
	public AbatementConfig(Integer guidPriceBegin, Integer guidPriceEnd, Integer abatementUnitPrice7,
			Integer abatementUnitPrice15, Integer abatementUnitPrice25, Integer abatementUnitPriceOther) {
		this.guidPriceBegin = guidPriceBegin;
		this.guidPriceEnd = guidPriceEnd;
		this.abatementUnitPrice7 = abatementUnitPrice7;
		this.abatementUnitPrice15 = abatementUnitPrice15;
		this.abatementUnitPrice25 = abatementUnitPrice25;
		this.abatementUnitPriceOther = abatementUnitPriceOther;
	}
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
	public Integer getAbatementUnitPrice7() {
		return abatementUnitPrice7;
	}
	public void setAbatementUnitPrice7(Integer abatementUnitPrice7) {
		this.abatementUnitPrice7 = abatementUnitPrice7;
	}
	public Integer getAbatementUnitPrice15() {
		return abatementUnitPrice15;
	}
	public void setAbatementUnitPrice15(Integer abatementUnitPrice15) {
		this.abatementUnitPrice15 = abatementUnitPrice15;
	}
	public Integer getAbatementUnitPrice25() {
		return abatementUnitPrice25;
	}
	public void setAbatementUnitPrice25(Integer abatementUnitPrice25) {
		this.abatementUnitPrice25 = abatementUnitPrice25;
	}
	public Integer getAbatementUnitPriceOther() {
		return abatementUnitPriceOther;
	}
	public void setAbatementUnitPriceOther(Integer abatementUnitPriceOther) {
		this.abatementUnitPriceOther = abatementUnitPriceOther;
	}
    
}
