package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderWaitReceiptException extends OrderException{

	private static String ERROR_CODE = "999922";
	
	private static String ERROR_MSG = "暂无法修改，请15分钟后再用";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderWaitReceiptException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
