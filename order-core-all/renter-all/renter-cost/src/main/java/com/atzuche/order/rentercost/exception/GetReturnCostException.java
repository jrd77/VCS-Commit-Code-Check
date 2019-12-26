package com.atzuche.order.rentercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class GetReturnCostException extends OrderException {
    public GetReturnCostException() {
        super(ErrorCode.COST_GET_RETUIRN_FAIL.getCode(), ErrorCode.COST_GET_RETUIRN_FAIL.getText());
    }
}
