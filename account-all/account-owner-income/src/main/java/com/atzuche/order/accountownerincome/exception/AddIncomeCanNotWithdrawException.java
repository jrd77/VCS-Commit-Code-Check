package com.atzuche.order.accountownerincome.exception;

import com.atzuche.order.commons.OrderException;

public class AddIncomeCanNotWithdrawException extends OrderException{

	private static String ERROR_CODE = "401234";
	
	private static String ERROR_MSG = "追加流水已操作，不能撤回";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddIncomeCanNotWithdrawException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
