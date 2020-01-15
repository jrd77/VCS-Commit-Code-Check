package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

public class SettleReturnWalletRemoteException extends OrderException {

    public SettleReturnWalletRemoteException() {
        super(ErrorCode.DEDUCT_WALLETR_MSG.getCode(), ErrorCode.DEDUCT_WALLETR_MSG.getCode());
    }
}
