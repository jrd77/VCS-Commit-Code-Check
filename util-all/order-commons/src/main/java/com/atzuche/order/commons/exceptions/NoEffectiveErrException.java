package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class NoEffectiveErrException extends OrderException{

    public NoEffectiveErrException() {
        super(ErrorCode.NO_EFFECTIVE_ERR.getCode(), ErrorCode.NO_EFFECTIVE_ERR.getText());
    }
}
