package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.autoyol.commons.web.ErrorCode;

public class CarDetailByFeignException extends SubmitOrderException {

    public CarDetailByFeignException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
