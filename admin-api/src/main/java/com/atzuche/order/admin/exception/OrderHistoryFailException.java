package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OrderHistoryFailException extends OrderException {
    public OrderHistoryFailException() {
        super(ErrorCode.ADMIN_ORDER_QUERY_HISTORY_ERR.getCode(), ErrorCode.ADMIN_ORDER_QUERY_HISTORY_ERR.getText());
    }
}
