package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class ImportAddIncomeExcelException extends OrderException{

	private static String ERROR_CODE = "100120";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImportAddIncomeExcelException(String msg) {
		super(ERROR_CODE, msg);
	}
}
