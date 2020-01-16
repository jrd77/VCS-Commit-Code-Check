package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerFinelFailException extends OrderException {
    public OwnerFinelFailException() {
        super(ErrorCode.OWNER_FINE_DETAIL_ERR.getCode(), ErrorCode.OWNER_FINE_DETAIL_ERR.getText());
    }
}
