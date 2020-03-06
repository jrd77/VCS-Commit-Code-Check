package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;

public class WithdrawalBalanceNotEnoughException extends OrderException{

	private static String ERROR_CODE = "400033";
	
	private static String ERROR_MSG = "可提现余额不足";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WithdrawalBalanceNotEnoughException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
