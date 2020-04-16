package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 10:33 上午
 **/
public class AccountDepositException extends OrderException {
    public AccountDepositException() {
        super(ErrorCode.DEPOSIT_NOT_FOUNT_ERR.getCode(), ErrorCode.DEPOSIT_NOT_FOUNT_ERR.getText());
    }
}
