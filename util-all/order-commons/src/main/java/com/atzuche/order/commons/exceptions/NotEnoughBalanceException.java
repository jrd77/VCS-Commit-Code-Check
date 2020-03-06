package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 9:49 上午
 **/
public class NotEnoughBalanceException extends OrderException {
    public NotEnoughBalanceException(String errorMsg) {
        super(ErrorCode.NOT_ENOUGH_BALANCE.getCode(), ErrorCode.NOT_ENOUGH_BALANCE.getText()+":"+errorMsg);
    }
}
