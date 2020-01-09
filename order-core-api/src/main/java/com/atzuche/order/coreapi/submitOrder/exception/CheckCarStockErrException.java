package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class CheckCarStockErrException extends SubmitOrderException  {

    public CheckCarStockErrException() {
        super(ErrorCode.FEIGN_CHECK_CAR_STOCK_ERROR.getCode(), ErrorCode.FEIGN_CHECK_CAR_STOCK_ERROR.getText());
    }
}
