package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderParameterException extends OrderException{
	
	private static String ERROR_CODE = "400400";
	
	private static String ERROR_MSG = "参数异常";

	/**
	 * 
	 */
	private static final long serialVersionUID = 9168644721168545868L;

	public ModifyOrderParameterException() {
		super(ERROR_CODE, ERROR_MSG);
	}
	
}