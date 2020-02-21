package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOwnerAddrOwnerOrderNotFindException extends OrderException{

	
	private static String ERROR_CODE = "540404";
	
	private static String ERROR_MSG = "未找到有效的车主订单";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOwnerAddrOwnerOrderNotFindException() {
		super(ERROR_CODE, ERROR_MSG);
	}

}
