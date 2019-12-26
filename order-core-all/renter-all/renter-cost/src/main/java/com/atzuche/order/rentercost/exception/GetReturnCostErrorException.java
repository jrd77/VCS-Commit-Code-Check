package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class GetReturnCostErrorException extends OrderException {
    public GetReturnCostErrorException() {
        super(ErrorCode.COST_GET_RETUIRN_ERROR.getCode(), ErrorCode.COST_GET_RETUIRN_ERROR.getText());
    }
}
