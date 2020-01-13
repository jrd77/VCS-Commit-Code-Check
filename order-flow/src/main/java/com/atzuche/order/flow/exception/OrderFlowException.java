package com.atzuche.order.flow.exception;


import com.atzuche.order.commons.OrderException;

public class OrderFlowException extends OrderException {

    private String errorCode;
    private String message;

    public OrderFlowException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
