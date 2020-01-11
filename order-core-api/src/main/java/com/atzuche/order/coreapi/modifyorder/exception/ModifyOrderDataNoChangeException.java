package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderDataNoChangeException extends OrderException{

private static String ERROR_CODE = "400504";
	
	private static String ERROR_MSG = "修改订单数据没有变化";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderDataNoChangeException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
