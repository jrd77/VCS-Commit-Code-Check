package com.atzuche.order.settle.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerCancelSettleException extends OrderException {
    public OwnerCancelSettleException() {
        super(ErrorCode.OWNER_SETTLE_ERR.getCode(), ErrorCode.OWNER_SETTLE_ERR.getText());
    }
}
