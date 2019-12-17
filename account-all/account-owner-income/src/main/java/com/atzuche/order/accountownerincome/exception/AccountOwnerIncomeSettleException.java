package com.atzuche.order.accountownerincome.exception;

import com.atzuche.order.accountownerincome.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountOwnerIncomeSettleException extends OrderException {
    public AccountOwnerIncomeSettleException() {
        super(ErrorCode.ACCOUT_OWNER_INCOME_SETTLE.getCode(), ErrorCode.ACCOUT_OWNER_INCOME_SETTLE.getCode());
    }
}
