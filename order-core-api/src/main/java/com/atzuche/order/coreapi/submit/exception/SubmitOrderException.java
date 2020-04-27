package com.atzuche.order.coreapi.submit.exception;

import com.atzuche.order.commons.OrderException;

/**
 * SubmitOrderController 相关业务校验异常
 *
 * @author pengcheng.fu
 * @date 2019/2/3 15:45
 */
public class SubmitOrderException extends OrderException {


    private static final long serialVersionUID = -1066843266437250882L;

    public SubmitOrderException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }


    public SubmitOrderException(String errorCode, String errorMsg, Object extra) {
        super(errorCode, errorMsg, extra);
    }
}
