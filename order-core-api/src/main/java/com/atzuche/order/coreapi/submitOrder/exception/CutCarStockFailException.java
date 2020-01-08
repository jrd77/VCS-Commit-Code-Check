package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class CutCarStockFailException extends SubmitOrderException  {

    public CutCarStockFailException() {
        super(ErrorCode.FEIGN_CUT_CAR_STOCK_FAIL.getCode(), ErrorCode.FEIGN_CUT_CAR_STOCK_FAIL.getText());
    }
}
