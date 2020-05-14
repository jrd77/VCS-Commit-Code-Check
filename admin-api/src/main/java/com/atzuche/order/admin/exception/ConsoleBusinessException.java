package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;

public class ConsoleBusinessException extends OrderException {

    private static final long serialVersionUID = 173061319412984253L;

    public ConsoleBusinessException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
