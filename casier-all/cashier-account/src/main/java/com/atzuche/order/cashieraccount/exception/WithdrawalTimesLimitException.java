package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;

public class WithdrawalTimesLimitException extends OrderException{

	private static String ERROR_CODE = "400032";
	
	private static String ERROR_MSG = "您今日提现机会已用完";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WithdrawalTimesLimitException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
