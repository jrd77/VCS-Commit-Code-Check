package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * 远程操作异常，透传参数
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/19 2:57 下午
 **/
public class RemoteCallException extends OrderException {
    public RemoteCallException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public RemoteCallException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }
}
