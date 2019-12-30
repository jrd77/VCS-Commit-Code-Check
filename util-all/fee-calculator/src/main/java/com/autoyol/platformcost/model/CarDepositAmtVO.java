package com.autoyol.platformcost.model;

import lombok.Data;

@Data
public class CarDepositAmtVO {

	/**
	 * 车辆押金
	 */
	private Integer carDepositAmt;
	/**
	 * 押金系数
	 */
	private Double carDepositRadio;
    /**
     * 品牌车押金系数（押金计算使用）
     */
    private Double carSpecialCoefficient;
    /**
     * 新车押金系数（押金计算使用）
     */
    private Double newCarCoefficient;
    /**
     *  X系数(基数)
     */
    private Integer suggestTotal;

}
