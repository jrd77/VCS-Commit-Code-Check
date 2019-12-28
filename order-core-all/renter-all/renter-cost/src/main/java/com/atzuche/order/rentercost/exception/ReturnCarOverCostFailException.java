package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class ReturnCarOverCostFailException extends OrderException {

    public ReturnCarOverCostFailException() {
        super(ErrorCode.IS_RETURN_CAR_OVER_FAIL.getCode(), ErrorCode.IS_RETURN_CAR_OVER_FAIL.getText());
    }
}
