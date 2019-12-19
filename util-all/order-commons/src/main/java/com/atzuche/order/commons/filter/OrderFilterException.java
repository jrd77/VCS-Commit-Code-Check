package com.atzuche.order.commons.filter;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 7:35 下午
 **/
public class OrderFilterException extends OrderException {
    public OrderFilterException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }

    public OrderFilterException(String errorCode, String errorMsg, Object extra, Throwable cause) {
        super(errorCode, errorMsg, extra, cause);
    }

    public OrderFilterException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }

    public OrderFilterException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
