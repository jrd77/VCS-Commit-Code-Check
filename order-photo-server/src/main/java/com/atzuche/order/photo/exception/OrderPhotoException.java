package com.atzuche.order.photo.exception;

import com.atzuche.order.commons.OrderException;

/**
 * Created by qincai.lin on 2020/1/16.
 */
public class OrderPhotoException extends OrderException {
    private String errorCode;
    private String message;

    public OrderPhotoException(String errorCode, String message) {
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
