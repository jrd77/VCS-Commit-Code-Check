package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OrderHistoryErrException extends OrderException {
    public OrderHistoryErrException() {
        super(ErrorCode.ADMIN_ORDER_QUERY_HISTORY_FAIL.getCode(), ErrorCode.ADMIN_ORDER_QUERY_HISTORY_FAIL.getText());
    }
}
