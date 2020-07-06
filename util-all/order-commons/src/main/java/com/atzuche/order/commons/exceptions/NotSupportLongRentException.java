package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class NotSupportLongRentException extends OrderException {

    public NotSupportLongRentException() {
        super(ErrorCode.NOT_SUPPORT_LONG.getCode(), ErrorCode.NOT_SUPPORT_LONG.getText());
    }
}
