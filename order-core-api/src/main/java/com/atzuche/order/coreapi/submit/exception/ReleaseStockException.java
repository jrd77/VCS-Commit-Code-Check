package com.atzuche.order.coreapi.submit.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class ReleaseStockException extends SubmitOrderException  {

    private static final long serialVersionUID = 8357789422156063851L;

    public ReleaseStockException() {
        super(ErrorCode.RELEASE_STOCK_FAIL.getCode(), ErrorCode.RELEASE_STOCK_FAIL.getText());
    }
}
