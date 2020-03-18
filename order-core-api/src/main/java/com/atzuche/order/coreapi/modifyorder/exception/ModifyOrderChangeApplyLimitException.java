package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderChangeApplyLimitException extends OrderException{

	private static String ERROR_CODE = "999921";
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderChangeApplyLimitException(String msg) {
		super(ERROR_CODE, msg);
	}
}
