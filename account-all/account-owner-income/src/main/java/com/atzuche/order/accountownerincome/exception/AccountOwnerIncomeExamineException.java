package com.atzuche.order.accountownerincome.exception;

import com.atzuche.order.accountownercost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountOwnerIncomeExamineException extends OrderException {
    public AccountOwnerIncomeExamineException() {
        super(ErrorCode.ACCOUT_OWNER_INCOME_EXAMINE.getCode(),ErrorCode.ACCOUT_OWNER_INCOME_EXAMINE.getCode());
    }
}
