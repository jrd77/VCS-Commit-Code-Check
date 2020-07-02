package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class DangerCountException extends OrderException {

    public DangerCountException(){
        super(ErrorCode.DANGER_COUNT_FAIL.getCode(),ErrorCode.DANGER_COUNT_FAIL.getText());
    }
}
