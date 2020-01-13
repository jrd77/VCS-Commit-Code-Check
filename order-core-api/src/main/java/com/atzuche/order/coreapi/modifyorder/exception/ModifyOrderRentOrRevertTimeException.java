package com.atzuche.order.coreapi.modifyorder.exception;


import com.atzuche.order.commons.OrderException;

public class ModifyOrderRentOrRevertTimeException extends OrderException{

	private static String ERROR_CODE = "400604";
	
	private static String ERROR_MSG = "取还车时间异常";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyOrderRentOrRevertTimeException(String msg) {
		super(ERROR_CODE, msg);
	}
}
