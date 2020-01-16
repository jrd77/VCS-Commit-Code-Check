package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerRenterPricelFailException extends OrderException {
    public OwnerRenterPricelFailException() {
        super(ErrorCode.OWNER_RENT_PRICE_FAIL.getCode(), ErrorCode.OWNER_RENT_PRICE_FAIL.getText());
    }
}
