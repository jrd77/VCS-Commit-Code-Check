package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class CanNotBuyException extends OrderException{

	private static String ERROR_CODE = "601233";
	
	private static String ERROR_MSG = "订单开始后不允许购买";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CanNotBuyException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
