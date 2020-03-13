package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class SupplementAmtException extends OrderException {

	private static String ERROR_CODE = "540700";
	
	private static String ERROR_MSG = "该状态下不支持添加负数";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SupplementAmtException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
