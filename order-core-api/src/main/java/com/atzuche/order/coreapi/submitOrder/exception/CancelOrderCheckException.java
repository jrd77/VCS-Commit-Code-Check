package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class CancelOrderCheckException extends OrderException {

    public CancelOrderCheckException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public CancelOrderCheckException() {
        super(ErrorCode.OWNER_COMFIRM_TRANS_FAILED.getCode(), ErrorCode.OWNER_COMFIRM_TRANS_FAILED.getText());
    }

    public CancelOrderCheckException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getText());
    }

}
