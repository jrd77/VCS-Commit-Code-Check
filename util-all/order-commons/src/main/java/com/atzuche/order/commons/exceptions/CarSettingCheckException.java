package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class CarSettingCheckException extends OrderException {

    public CarSettingCheckException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

}
