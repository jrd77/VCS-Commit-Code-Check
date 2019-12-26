package com.atzuche.order.rentermem.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.rentermem.enums.RenterMemErrorCodeEnum;

public class CalWzDepositAmtException extends OrderException {

    public CalWzDepositAmtException() {
        super(RenterMemErrorCodeEnum.RENTER_MEMBER_RIGHT_CAL_WZ_ERROR.getCode(), RenterMemErrorCodeEnum.RENTER_MEMBER_RIGHT_CAL_WZ_ERROR.getText());
    }
}
