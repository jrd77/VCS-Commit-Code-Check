package com.atzuche.order.settle.exception;

import com.atzuche.order.commons.OrderException;
import com.autoyol.commons.web.ErrorCode;

public class CancelOrderSettleParamException extends OrderException {
    public CancelOrderSettleParamException() {
        super(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
    }
}
