package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class GetCarOverCostFailException extends OrderException {

    public GetCarOverCostFailException() {
        super(ErrorCode.IS_GET_CAR_OVER_FAIL.getCode(), ErrorCode.IS_GET_CAR_OVER_FAIL.getText());
    }
}
