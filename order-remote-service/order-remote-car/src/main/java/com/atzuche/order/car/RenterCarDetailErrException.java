package com.atzuche.order.car;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class RenterCarDetailErrException extends OrderException {

    public RenterCarDetailErrException() {
        super(ErrorCode.FEIGN_RENTER_CAR_ERROR.getCode(),ErrorCode.FEIGN_RENTER_CAR_ERROR.getText());
    }
}
