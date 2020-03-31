package com.atzuche.order.coreapi.submit.exception;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 7:35 下午
 **/
public class OrderCostFilterException extends OrderException {
    
    private static final long serialVersionUID = 1965233888831012201L;

    public OrderCostFilterException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }

    public OrderCostFilterException(String errorCode, String errorMsg, Object extra, Throwable cause) {
        super(errorCode, errorMsg, extra, cause);
    }

    public OrderCostFilterException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }

    public OrderCostFilterException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
