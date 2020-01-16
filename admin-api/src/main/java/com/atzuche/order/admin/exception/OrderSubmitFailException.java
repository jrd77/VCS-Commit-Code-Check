package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OrderSubmitFailException extends OrderException {
    public OrderSubmitFailException() {
        super(ErrorCode.ADMIN_ORDER_SUBMIT_FAIL.getCode(), ErrorCode.ADMIN_ORDER_SUBMIT_FAIL.getText());
    }
}
