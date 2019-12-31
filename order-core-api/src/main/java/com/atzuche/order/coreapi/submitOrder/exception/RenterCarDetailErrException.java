package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class RenterCarDetailErrException extends SubmitOrderException {

    public RenterCarDetailErrException() {
        super(ErrorCode.FEIGN_RENTER_CAR_ERROR.getCode(),ErrorCode.FEIGN_RENTER_CAR_ERROR.getText());
    }
}
