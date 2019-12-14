package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.autoyol.commons.web.ErrorCode;

public class CarDetailByFeignException extends SubmitOrderException {
    public CarDetailByFeignException(String msg) {
        super(msg);
    }

    public CarDetailByFeignException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CarDetailByFeignException(ErrorCode errorCode, String msg) {
        super(errorCode, msg);
    }
}
