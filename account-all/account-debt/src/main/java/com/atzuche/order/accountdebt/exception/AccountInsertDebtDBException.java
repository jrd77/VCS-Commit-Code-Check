package com.atzuche.order.accountdebt.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

@Data
public class AccountInsertDebtDBException extends OrderException {

    public AccountInsertDebtDBException() {
        super(ErrorCode.ACCOUT_DEBT_INSERT_DEBT.getCode(),ErrorCode.ACCOUT_DEBT_INSERT_DEBT.getCode());
    }
}
