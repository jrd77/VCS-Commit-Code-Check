package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class TransferCarException extends OrderException{

	private static String ERROR_CODE = "400804";
	
	private static String ERROR_MSG = "不能换同一辆车";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransferCarException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
