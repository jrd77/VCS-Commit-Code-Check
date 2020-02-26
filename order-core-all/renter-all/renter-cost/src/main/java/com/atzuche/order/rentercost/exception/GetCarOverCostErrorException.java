package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class GetCarOverCostErrorException extends OrderException {

    public GetCarOverCostErrorException() {
        super(ErrorCode.IS_GET_CAR_OVER_ERROR.getCode(), ErrorCode.IS_GET_CAR_OVER_ERROR.getText());
    }
}
