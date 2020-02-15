package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderCurrentStatusNotSupportException extends OrderException{

private static String ERROR_CODE = "550800";
	
	private static String ERROR_MSG = "当前订单状态不支持修改订单";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderCurrentStatusNotSupportException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
