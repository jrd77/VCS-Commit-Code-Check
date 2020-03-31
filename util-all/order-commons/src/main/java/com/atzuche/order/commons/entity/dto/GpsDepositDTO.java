package com.atzuche.order.commons.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GpsDepositDTO {

	/**
	 * 是否收取押金,1是， 2否
	 */
	private Integer hwDepositFlag;
	/**
	 * 车载硬件收取押金计费
	 */
	private Integer hwDepositBill;
	/**
	 * 车载硬件收取押金金额
	 */
	private Integer hwDepositTotal;
	/**
	 * 车主真实收益
	 */
	private Integer ownerRealIncome;
}
