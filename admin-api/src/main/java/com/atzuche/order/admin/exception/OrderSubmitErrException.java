package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OrderSubmitErrException extends OrderException {
    public OrderSubmitErrException() {
        super(ErrorCode.ADMIN_ORDER_SUBMIT_ERR.getCode(), ErrorCode.ADMIN_ORDER_SUBMIT_ERR.getText());
    }
}
