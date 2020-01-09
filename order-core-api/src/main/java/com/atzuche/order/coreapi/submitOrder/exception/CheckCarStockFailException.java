package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class CheckCarStockFailException extends SubmitOrderException  {

    public CheckCarStockFailException() {
        super(ErrorCode.FEIGN_CHECK_CAR_STOCK_FAIL.getCode(), ErrorCode.FEIGN_CHECK_CAR_STOCK_FAIL.getText());
    }
}
