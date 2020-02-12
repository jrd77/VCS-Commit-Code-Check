package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerRentDetailErrException extends OrderException {
    public OwnerRentDetailErrException() {
        super(ErrorCode.OWNER_PLATFORM_ERR.getCode(), ErrorCode.OWNER_PLATFORM_ERR.getText());
    }
}
