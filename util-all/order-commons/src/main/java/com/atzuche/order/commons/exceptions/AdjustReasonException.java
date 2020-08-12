package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class AdjustReasonException  extends OrderException {

    public AdjustReasonException() {
        super(ErrorCode.ADJUST_REASON_ERR.getCode(), ErrorCode.ADJUST_REASON_ERR.getText());
    }
}
