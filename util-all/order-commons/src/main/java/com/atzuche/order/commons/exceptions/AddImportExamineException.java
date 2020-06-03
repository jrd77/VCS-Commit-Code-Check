package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class AddImportExamineException extends OrderException{

	private static String ERROR_CODE = "100130";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddImportExamineException(String msg) {
		super(ERROR_CODE, msg);
	}
}
