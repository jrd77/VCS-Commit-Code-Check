package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

public class GetWalletRemoteException extends OrderException {

    public GetWalletRemoteException() {
        super(ErrorCode.GET_WALLETR_MSG.getCode(), ErrorCode.GET_WALLETR_MSG.getCode());
    }
}
