package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class RenterCostFailException extends OrderException {
    public RenterCostFailException() {
        super(ErrorCode.RENTER_COST_DETAIL_FAIL.getCode(), ErrorCode.RENTER_COST_DETAIL_FAIL.getText());
    }
}
