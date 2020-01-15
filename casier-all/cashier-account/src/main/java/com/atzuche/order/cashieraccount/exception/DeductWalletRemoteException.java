package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

public class DeductWalletRemoteException extends OrderException {

    public DeductWalletRemoteException() {
        super(ErrorCode.DEDUCT_WALLETR_MSG.getCode(), ErrorCode.DEDUCT_WALLETR_MSG.getCode());
    }
}
