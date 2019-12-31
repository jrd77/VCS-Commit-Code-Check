package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

public class RenterCarDetailFailException extends SubmitOrderException {

    public RenterCarDetailFailException() {
        super(ErrorCode.FEIGN_RENTER_CAR_FAIL.getCode(),ErrorCode.FEIGN_RENTER_CAR_FAIL.getText());
    }
}
