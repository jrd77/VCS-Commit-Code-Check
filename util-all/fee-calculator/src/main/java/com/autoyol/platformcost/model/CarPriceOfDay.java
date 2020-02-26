package com.autoyol.platformcost.model;

import java.time.LocalDate;

public class CarPriceOfDay {

	/**
	 * 日期
	 */
    private LocalDate curDate;
    /**
              * 对应的价格
     */
    private Integer dayPrice;
	
	
	public LocalDate getCurDate() {
		return curDate;
	}
	public void setCurDate(LocalDate curDate) {
		this.curDate = curDate;
	}
	public Integer getDayPrice() {
		return dayPrice;
	}
	public void setDayPrice(Integer dayPrice) {
		this.dayPrice = dayPrice;
	} 
    
}
