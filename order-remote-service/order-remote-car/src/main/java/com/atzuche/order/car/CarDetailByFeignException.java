package com.atzuche.order.car;

import com.atzuche.order.commons.OrderException;

public class CarDetailByFeignException extends OrderException {

    public CarDetailByFeignException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
