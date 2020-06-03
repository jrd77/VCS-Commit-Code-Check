package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class NotFoundCashierException extends OrderException {

    public NotFoundCashierException() {
        super(ErrorCode.PAY_TRANS_NO_NOT_FOUNT_CASHIER.getCode(), ErrorCode.PAY_TRANS_NO_NOT_FOUNT_CASHIER.getText());
    }
}
