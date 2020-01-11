package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class ReleaseStockException extends SubmitOrderException  {

    public ReleaseStockException() {
        super(ErrorCode.RELEASE_STOCK_FAIL.getCode(), ErrorCode.RELEASE_STOCK_FAIL.getText());
    }
}
