package com.atzuche.order.accountplatorm.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 结算平台收益补贴出错
 */
@EqualsAndHashCode(callSuper = false)
public class AccountPlatormException extends OrderException {
    public AccountPlatormException() {
        super(ErrorCode.ACCOUT_OWNER_INCOME_EXAMINE.getCode(), ErrorCode.ACCOUT_OWNER_INCOME_EXAMINE.getCode());
    }
}
