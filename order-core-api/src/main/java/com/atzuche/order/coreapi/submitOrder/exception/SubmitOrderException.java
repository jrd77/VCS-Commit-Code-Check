package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.OrderException;
import com.autoyol.commons.web.ErrorCode;

public class SubmitOrderException extends OrderException {


    public SubmitOrderException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }


    public SubmitOrderException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }
}
