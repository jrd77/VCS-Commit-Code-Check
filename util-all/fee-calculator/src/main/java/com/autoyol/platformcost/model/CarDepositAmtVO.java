package com.autoyol.platformcost.model;

import lombok.Data;

@Data
public class CarDepositAmtVO {

	/**
	 * 车辆押金
	 */
	private Integer carDepositAmt;
	/**
	 * 减免比例
	 */
	//private Double reliefPercetage;
	/**
	 * 押金系数
	 */
	private Double carDepositRadio;
    /**
     * 车型品牌系数
     */
    private Double carBrandTypeRadio;
    /**
     * 车辆年份系数
     */
    private Double carYearRadio;
    /**
     *  X系数
     */
    private Integer suggestTotal;
	
}
