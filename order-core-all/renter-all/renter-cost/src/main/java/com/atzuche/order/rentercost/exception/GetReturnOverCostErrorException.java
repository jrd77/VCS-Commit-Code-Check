package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class GetReturnOverCostErrorException extends OrderException {
    public GetReturnOverCostErrorException() {
        super(ErrorCode.COST_GET_RETUIRN_OVER_ERROR.getCode(), ErrorCode.COST_GET_RETUIRN_OVER_ERROR.getText());
    }
}
