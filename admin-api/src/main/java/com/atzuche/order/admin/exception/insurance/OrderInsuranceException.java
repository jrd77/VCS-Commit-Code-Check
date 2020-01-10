package com.atzuche.order.admin.exception.insurance;


import com.atzuche.order.commons.OrderException;

public class OrderInsuranceException extends OrderException {

    private String errorCode;
    private String message;

    public OrderInsuranceException(String errorCode, String message) {
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
