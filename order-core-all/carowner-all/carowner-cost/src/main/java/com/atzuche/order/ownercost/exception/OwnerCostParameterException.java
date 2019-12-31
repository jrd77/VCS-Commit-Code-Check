package com.atzuche.order.ownercost.exception;

import com.atzuche.order.commons.OrderException;

public class OwnerCostParameterException extends OrderException{
	
	private static String ERROR_CODE = "400400";
	
	private static String ERROR_MSG = "参数异常";

	/**
	 * 
	 */
	private static final long serialVersionUID = 9168644721168545868L;

	public OwnerCostParameterException() {
		super(ERROR_CODE, ERROR_MSG);
	}
	
}
