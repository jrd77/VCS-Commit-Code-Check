package com.atzuche.order.coreapi.submitOrder.exception;

import com.autoyol.commons.web.ErrorCode;

public class SubmitOrderException extends Exception {
	
	private static final long serialVersionUID = -4509803374176910835L;
	
	private ErrorCode errorCode = ErrorCode.FAILED;
	
	public SubmitOrderException(String msg){  
        super(msg);  
    } 
	
	public SubmitOrderException(ErrorCode errorCode){
		this.errorCode = errorCode;
	}
	
	public SubmitOrderException(ErrorCode errorCode, String msg){
		super(msg);
		this.errorCode = errorCode;
	}
	

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	} 

}
