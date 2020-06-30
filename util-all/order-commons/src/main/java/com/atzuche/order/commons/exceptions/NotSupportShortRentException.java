package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class NotSupportShortRentException extends OrderException {

    public NotSupportShortRentException() {
        super(ErrorCode.NOT_SUPPORT_SHORT.getCode(), ErrorCode.NOT_SUPPORT_SHORT.getText());
    }
}
