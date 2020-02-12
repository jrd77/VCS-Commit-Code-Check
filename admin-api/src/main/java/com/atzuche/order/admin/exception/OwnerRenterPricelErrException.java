package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerRenterPricelErrException extends OrderException {
    public OwnerRenterPricelErrException() {
        super(ErrorCode.OWNER_RENT_PRICE_ERR.getCode(), ErrorCode.OWNER_RENT_PRICE_ERR.getText());
    }
}
