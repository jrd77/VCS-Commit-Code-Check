package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderAlreadyOverException extends OrderException{

	private static String ERROR_CODE = "9000022";
	
	private static String ERROR_MSG = "订单已结束,无法修改";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderAlreadyOverException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
