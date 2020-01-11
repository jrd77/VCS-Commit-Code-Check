package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class ReleaseCarStockErrException extends SubmitOrderException  {

    public ReleaseCarStockErrException() {
        super(ErrorCode.FEIGN_RELEASE_CAR_STOCK_ERROR.getCode(), ErrorCode.FEIGN_RELEASE_CAR_STOCK_ERROR.getText());
    }
}
