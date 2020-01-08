package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class CutCarStockErrException extends SubmitOrderException  {

    public CutCarStockErrException() {
        super(ErrorCode.FEIGN_CUT_CAR_STOCK_ERROR.getCode(), ErrorCode.FEIGN_CUT_CAR_STOCK_ERROR.getText());
    }
}
