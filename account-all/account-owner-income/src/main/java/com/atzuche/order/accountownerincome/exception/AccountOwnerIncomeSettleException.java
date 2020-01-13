package com.atzuche.order.accountownerincome.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 结算产生待审核收益 落库失败
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AccountOwnerIncomeSettleException extends OrderException {
    public AccountOwnerIncomeSettleException() {
        super(ErrorCode.ACCOUT_OWNER_INCOME_SETTLE.getCode(), ErrorCode.ACCOUT_OWNER_INCOME_SETTLE.getCode());
    }
}
