package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
public class CashierRefundApplyException extends OrderException {

    public CashierRefundApplyException() {
        super(ErrorCode.CASHIER_REFUND_APPLY.getCode(), ErrorCode.CASHIER_REFUND_APPLY.getText());
    }
}
