package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderMemberNotExistException extends OrderException{

	private static String ERROR_CODE = "400404";
	
	private static String ERROR_MSG = "会员不存在";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderMemberNotExistException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
