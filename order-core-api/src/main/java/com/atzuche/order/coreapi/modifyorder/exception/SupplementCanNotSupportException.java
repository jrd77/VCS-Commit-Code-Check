package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class SupplementCanNotSupportException extends OrderException{

	private static String ERROR_CODE = "5400800";
	
	private static String ERROR_MSG = "租车费用/车辆押金未结算不支持增加补付";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SupplementCanNotSupportException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
