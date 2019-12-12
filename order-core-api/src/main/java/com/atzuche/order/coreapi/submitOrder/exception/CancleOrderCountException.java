package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.coreapi.exception.SubmitOrderException;
import com.autoyol.commons.web.ErrorCode;
/*
 * @Author ZhangBin
 * @Date 2019/12/12 16:20
 * @Description: 取消订单次数校验异常类
 *
 **/
public class CancleOrderCountException extends SubmitOrderException {
    public CancleOrderCountException(String msg) {
        super(msg);
    }

    public CancleOrderCountException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CancleOrderCountException(ErrorCode errorCode, String msg) {
        super(errorCode, msg);
    }
}
