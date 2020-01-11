package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderChangeApplyNotFindException extends OrderException{
	
	private static String ERROR_CODE = "400404";
	
	private static String ERROR_MSG = "未找到有效的修改申请记录";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderChangeApplyNotFindException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
