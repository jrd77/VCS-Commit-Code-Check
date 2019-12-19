package com.atzuche.order.coreapi.submitOrder.exception;

public class CarDetailByFeignException extends SubmitOrderException {

    public CarDetailByFeignException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
