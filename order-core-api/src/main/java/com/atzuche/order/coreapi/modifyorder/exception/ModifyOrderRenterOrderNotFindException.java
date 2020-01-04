package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderRenterOrderNotFindException extends OrderException{
private static String ERROR_CODE = "400404";
	
	private static String ERROR_MSG = "未找到租客订单";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderRenterOrderNotFindException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
