package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class RenterDriverFailException extends SubmitOrderException  {

    public RenterDriverFailException() {
        super(ErrorCode.MEMBER_DRIVER_fAIL.getCode(), ErrorCode.MEMBER_DRIVER_fAIL.getText());
    }
}
