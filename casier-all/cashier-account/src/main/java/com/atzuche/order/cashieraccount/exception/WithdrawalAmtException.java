package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;

public class WithdrawalAmtException extends OrderException{

	private static String ERROR_CODE = "400030";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WithdrawalAmtException(String msg) {
		super(ERROR_CODE, msg);
	}
}
