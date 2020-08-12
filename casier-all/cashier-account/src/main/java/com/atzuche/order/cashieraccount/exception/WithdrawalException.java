package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;

public class WithdrawalException extends OrderException{


    private static final long serialVersionUID = -2690232226905569553L;

    public WithdrawalException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }

    public WithdrawalException(String errorCode, String errorMsg, Object extra, Throwable cause) {
        super(errorCode, errorMsg, extra, cause);
    }

    public WithdrawalException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }

    public WithdrawalException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
