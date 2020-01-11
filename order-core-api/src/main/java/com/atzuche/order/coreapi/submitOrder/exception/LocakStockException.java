package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class LocakStockException extends SubmitOrderException  {

    public LocakStockException() {
        super(ErrorCode.LOCK_STOCK_FAIL.getCode(), ErrorCode.LOCK_STOCK_FAIL.getText());
    }
}
