package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class OrderDetailException extends SubmitOrderException   {
    public OrderDetailException() {
        super(ErrorCode.ORDER_QUERY_FAIL.getCode(), ErrorCode.ORDER_QUERY_FAIL.getText());
    }
}
