package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OrderRHistoryErrException extends OrderException {
    public OrderRHistoryErrException() {
        super(ErrorCode.ADMIN_ORDER_QUERY_R_HISTORY_ERR.getCode(), ErrorCode.ADMIN_ORDER_QUERY_R_HISTORY_ERR.getText());
    }
}
