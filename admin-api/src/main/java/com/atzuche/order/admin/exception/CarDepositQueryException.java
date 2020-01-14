package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class CarDepositQueryException  extends OrderException {
    public CarDepositQueryException() {
        super(ErrorCode.ADMIN_CAR_DEPOSIT_QUERY_FAIL.getCode(), ErrorCode.ADMIN_CAR_DEPOSIT_QUERY_FAIL.getText());
    }
}
