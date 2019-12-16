package com.atzuche.order.accountdebt.exception;

import com.atzuche.order.accountdebt.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountDeductDebtDBException extends OrderException {


    public AccountDeductDebtDBException() {
        super(ErrorCode.ACCOUT_DEBT_DEDUCT_DEBT.getCode(),ErrorCode.ACCOUT_DEBT_DEDUCT_DEBT.getCode());
    }
}
