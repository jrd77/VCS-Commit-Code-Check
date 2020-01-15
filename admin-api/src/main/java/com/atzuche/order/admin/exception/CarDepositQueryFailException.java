package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class CarDepositQueryFailException extends OrderException {
    public CarDepositQueryFailException() {
        super(ErrorCode.ADMIN_CAR_DEPOSIT_QUERY_FAIL.getCode(), ErrorCode.ADMIN_CAR_DEPOSIT_QUERY_FAIL.getText());
    }
}
