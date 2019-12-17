package com.atzuche.order.accountdebt.exception;

import com.atzuche.order.accountdebt.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountInsertDebtDBException extends OrderException {

    public AccountInsertDebtDBException(ErrorCode errorCode) {
        super(errorCode.getCode(),errorCode.getText());
    }

    public AccountInsertDebtDBException() {
        super(ErrorCode.ACCOUT_DEBT_INSERT_DEBT.getCode(),ErrorCode.ACCOUT_DEBT_INSERT_DEBT.getCode());
    }
}
