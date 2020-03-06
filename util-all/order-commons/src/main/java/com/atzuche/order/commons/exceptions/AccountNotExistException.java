package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 10:33 上午
 **/
public class AccountNotExistException extends OrderException {
    public AccountNotExistException(String errorMsg) {
        super(ErrorCode.NOT_EXIST_ACCOUNT.getCode(), ErrorCode.NOT_EXIST_ACCOUNT.getText()+":"+errorMsg);
    }
}
