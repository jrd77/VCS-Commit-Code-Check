package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.cashieraccount.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;
@Data
public class CashierRefundApplyException extends OrderException {

    public CashierRefundApplyException() {
        super(ErrorCode.CASHIER_REFUND_APPLY.getCode(), ErrorCode.CASHIER_REFUND_APPLY.getCode());
    }
}
