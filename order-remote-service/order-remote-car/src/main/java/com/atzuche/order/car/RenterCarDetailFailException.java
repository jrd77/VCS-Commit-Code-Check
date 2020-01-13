package com.atzuche.order.car;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class RenterCarDetailFailException extends OrderException {

    public RenterCarDetailFailException() {
        super(ErrorCode.FEIGN_RENTER_CAR_FAIL.getCode(),ErrorCode.FEIGN_RENTER_CAR_FAIL.getText());
    }


    public RenterCarDetailFailException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
