package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOwnerAddrCheckException extends OrderException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ModifyOwnerAddrCheckException(String errorCode, String errorMsg) {
		super(errorCode, errorMsg);
	}

}
