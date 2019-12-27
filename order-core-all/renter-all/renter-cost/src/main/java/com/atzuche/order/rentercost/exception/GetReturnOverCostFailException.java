package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class GetReturnOverCostFailException extends OrderException {
    public GetReturnOverCostFailException() {
        super(ErrorCode.COST_GET_RETUIRN_OVER_FAIL.getCode(), ErrorCode.COST_GET_RETUIRN_OVER_FAIL.getText());
    }
}
