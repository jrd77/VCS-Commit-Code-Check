package com.atzuche.order.rentermem.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.rentermem.enums.RenterMemErrorCodeEnum;

public class CalCarDepositAmtException extends OrderException {

    public CalCarDepositAmtException() {
        super(RenterMemErrorCodeEnum.RENTER_MEMBER_RIGHT_CAL_CAR_ERROR.getCode(), RenterMemErrorCodeEnum.RENTER_MEMBER_RIGHT_CAL_CAR_ERROR.getText());
    }
}
