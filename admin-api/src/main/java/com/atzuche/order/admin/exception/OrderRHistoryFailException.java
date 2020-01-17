package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OrderRHistoryFailException extends OrderException {
    public OrderRHistoryFailException() {
        super(ErrorCode.ADMIN_ORDER_QUERY_R_HISTORY_FAIL.getCode(), ErrorCode.ADMIN_ORDER_QUERY_R_HISTORY_FAIL.getText());
    }
}
