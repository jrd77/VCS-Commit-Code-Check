package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class TransferUseOwnerCouponException extends OrderException{


	private static String ERROR_CODE = "4007604";
	
	private static String ERROR_MSG = "使用了车主券不能换车";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransferUseOwnerCouponException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
