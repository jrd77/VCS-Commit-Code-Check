package com.atzuche.order.settle.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccountInsertDebtDBException extends OrderException {

    public AccountInsertDebtDBException() {
        super(ErrorCode.ACCOUT_DEBT_INSERT_DEBT.getCode(),ErrorCode.ACCOUT_DEBT_INSERT_DEBT.getCode());
    }
}
