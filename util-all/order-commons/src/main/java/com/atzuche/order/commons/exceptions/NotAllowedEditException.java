package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class NotAllowedEditException extends OrderException {
    public NotAllowedEditException() {
        super(ErrorCode.NOT_ALLOWED_EDIT.getCode(), ErrorCode.NOT_ALLOWED_EDIT.getText());
    }
}
