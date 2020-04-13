package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderLongRentDaysLimitException extends OrderException{

	private static String ERROR_CODE = "600601";
	
	private static String ERROR_MSG = "长租订单租期需不少于30天";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderLongRentDaysLimitException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
