package com.atzuche.order.settle.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

public class OrderSettleFlatAccountException extends OrderException {

    public OrderSettleFlatAccountException() {
        super(ErrorCode.ORDER_SETTLE_FLAT_ACCOUNT.getCode(), ErrorCode.ORDER_SETTLE_FLAT_ACCOUNT.getCode());
    }
}
