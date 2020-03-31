package com.atzuche.order.coreapi.submit.exception;

import com.atzuche.order.commons.OrderException;

public class SubmitOrderException extends OrderException {


    private static final long serialVersionUID = -1066843266437250882L;

    public SubmitOrderException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }


    public SubmitOrderException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }
}
