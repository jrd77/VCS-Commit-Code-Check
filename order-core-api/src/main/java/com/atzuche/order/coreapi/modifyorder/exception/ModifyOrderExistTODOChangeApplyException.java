package com.atzuche.order.coreapi.modifyorder.exception;

import com.atzuche.order.commons.OrderException;

public class ModifyOrderExistTODOChangeApplyException extends OrderException{

	private static String ERROR_CODE = "400604";
	
	private static String ERROR_MSG = "存在未处理的修改申请记录不能再修改订单";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderExistTODOChangeApplyException() {
		super(ERROR_CODE, ERROR_MSG);
	}
}
