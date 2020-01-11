package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderParentOrderNotFindException extends OrderException{
	
	private static String ERROR_CODE = "400404";
	
	private static String ERROR_MSG = "主订单不存在或不是该会员订单";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderParentOrderNotFindException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
