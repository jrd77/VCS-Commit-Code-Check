package com.atzuche.order.settle.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

@Data
public class AccountDeductDebtDBException extends OrderException {


    public AccountDeductDebtDBException() {
        super(ErrorCode.ACCOUT_DEBT_DEDUCT_DEBT.getCode(), ErrorCode.ACCOUT_DEBT_DEDUCT_DEBT.getCode());
    }
}
