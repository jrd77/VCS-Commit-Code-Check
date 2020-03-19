package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
public class CashierPayApplyException extends OrderException {

    public CashierPayApplyException() {
        super(ErrorCode.CASHIER_PAY_APPLY.getCode(), ErrorCode.CASHIER_PAY_APPLY.getCode());
    }
}
