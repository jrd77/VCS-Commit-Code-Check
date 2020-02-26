package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class ReturnCarOverCostErrorException extends OrderException {

    public ReturnCarOverCostErrorException() {
        super(ErrorCode.IS_RETURN_CAR_OVER_ERROR.getCode(), ErrorCode.IS_RETURN_CAR_OVER_ERROR.getText());
    }
}
