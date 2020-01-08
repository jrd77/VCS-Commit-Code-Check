package com.atzuche.order.coreapi.submitOrder.exception;

public class NotStockException extends SubmitOrderException  {

    public NotStockException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
