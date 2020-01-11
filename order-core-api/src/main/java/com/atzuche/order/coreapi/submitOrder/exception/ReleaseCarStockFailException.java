package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class ReleaseCarStockFailException extends SubmitOrderException  {

    public ReleaseCarStockFailException() {
        super(ErrorCode.FEIGN_RELEASE_CAR_STOCK_FAIL.getCode(), ErrorCode.FEIGN_RELEASE_CAR_STOCK_FAIL.getText());
    }
}
