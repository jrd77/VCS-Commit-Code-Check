package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 4:09 下午
 **/
public class OrderCancelFailException extends OrderException {
    public OrderCancelFailException() {
        super(ErrorCode.ADMIN_ORDER_CANCEL_FAIL.getCode(), ErrorCode.ADMIN_ORDER_CANCEL_FAIL.getCode());
    }

}
