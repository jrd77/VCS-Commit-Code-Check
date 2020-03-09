package com.atzuche.order.settle.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class RenterCancelSettleException extends OrderException {
    public RenterCancelSettleException() {
        super(ErrorCode.RENTER_SETTLE_ERR.getCode(), ErrorCode.RENTER_SETTLE_ERR.getText());
    }
}
