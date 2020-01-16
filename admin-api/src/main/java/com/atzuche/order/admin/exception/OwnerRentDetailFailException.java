package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerRentDetailFailException extends OrderException {
    public OwnerRentDetailFailException() {
        super(ErrorCode.OWNER_RENT_DETAIL_FAIL.getCode(), ErrorCode.OWNER_RENT_DETAIL_FAIL.getText());
    }
}
