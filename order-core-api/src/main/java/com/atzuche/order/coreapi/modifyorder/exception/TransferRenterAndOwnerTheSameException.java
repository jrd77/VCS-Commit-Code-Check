package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class TransferRenterAndOwnerTheSameException extends OrderException{

	private static String ERROR_CODE = "400807";
	
	private static String ERROR_MSG = "不能换自己的车";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransferRenterAndOwnerTheSameException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
