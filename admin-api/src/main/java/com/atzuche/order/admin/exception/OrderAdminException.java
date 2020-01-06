package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 4:09 下午
 **/
public class OrderAdminException extends OrderException {
    public OrderAdminException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }

    public OrderAdminException(String errorCode, String errorMsg, Object extra, Throwable cause) {
        super(errorCode, errorMsg, extra, cause);
    }

    public OrderAdminException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }

    public OrderAdminException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
