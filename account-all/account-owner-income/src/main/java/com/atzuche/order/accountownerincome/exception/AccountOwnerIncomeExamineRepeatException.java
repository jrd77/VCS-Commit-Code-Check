package com.atzuche.order.accountownerincome.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.EqualsAndHashCode;

/**
 * 收益审核失败异常
 */
@EqualsAndHashCode(callSuper = false)
public class AccountOwnerIncomeExamineRepeatException extends OrderException {
    public AccountOwnerIncomeExamineRepeatException() {
        super(ErrorCode.OWNER_INCOM_AUDIT_REPEAT_FAIL.getCode(), ErrorCode.OWNER_INCOM_AUDIT_REPEAT_FAIL.getText());
    }
}
