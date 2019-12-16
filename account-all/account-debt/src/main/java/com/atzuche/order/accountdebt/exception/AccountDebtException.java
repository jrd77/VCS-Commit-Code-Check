package com.atzuche.order.accountdebt.exception;

import com.atzuche.order.commons.OrderException;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;

@Data
public class AccountDebtException extends OrderException {

    public AccountDebtException(ErrorCode errorCode) {
        super(errorCode.getCode(),errorCode.getText());
    }
}
