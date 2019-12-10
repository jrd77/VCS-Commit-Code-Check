package com.autoyol.platformcost.model;

public class CarPriceOfDay {

	/**
	 * 时间字符串，格式为：yyyyMMdd
	 */
    private String dateStr;
    /**
              * 对应的价格
     */
    private Integer dayPrice;
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public Integer getDayPrice() {
		return dayPrice;
	}
	public void setDayPrice(Integer dayPrice) {
		this.dayPrice = dayPrice;
	} 
    
}
