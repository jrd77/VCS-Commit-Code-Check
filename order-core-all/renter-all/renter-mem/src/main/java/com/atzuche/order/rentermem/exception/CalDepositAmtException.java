package com.atzuche.order.rentermem.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.rentermem.enums.RenterMemErrorCodeEnum;

public class CalDepositAmtException extends OrderException {

    public CalDepositAmtException() {
        super(RenterMemErrorCodeEnum.RENTER_MEMBER_RIGHT_CAL_ERROR.getCode(), RenterMemErrorCodeEnum.RENTER_MEMBER_RIGHT_CAL_ERROR.getText());
    }
}
