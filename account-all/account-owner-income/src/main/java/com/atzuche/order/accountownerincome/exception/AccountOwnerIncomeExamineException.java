package com.atzuche.order.accountownerincome.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收益审核失败异常
 */
@EqualsAndHashCode(callSuper = false)
public class AccountOwnerIncomeExamineException extends OrderException {
    public AccountOwnerIncomeExamineException() {
        super(ErrorCode.ACCOUT_OWNER_INCOME_EXAMINE.getCode(), ErrorCode.ACCOUT_OWNER_INCOME_EXAMINE.getCode());
    }
}