package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class RenterDriverErrException extends SubmitOrderException  {

    public RenterDriverErrException() {
        super(ErrorCode.FEIGN_MEMBER_DRIVER_ERROR.getCode(), ErrorCode.FEIGN_MEMBER_DRIVER_ERROR.getText());
    }
}
