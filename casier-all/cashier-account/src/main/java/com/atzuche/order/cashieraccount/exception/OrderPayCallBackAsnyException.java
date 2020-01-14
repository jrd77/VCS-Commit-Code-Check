package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

public class OrderPayCallBackAsnyException extends OrderException {

    public OrderPayCallBackAsnyException() {
        super(ErrorCode.CASHIER_PAY_CALL_BACK_FAIL.getCode(), ErrorCode.CASHIER_PAY_CALL_BACK_FAIL.getCode());
    }
}
