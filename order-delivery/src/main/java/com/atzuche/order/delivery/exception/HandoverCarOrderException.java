package com.atzuche.order.delivery.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.delivery.common.DeliveryErrorCode;

/**
 * 交接车业务异常
 *
 * @author 胡春林
 **/
public class HandoverCarOrderException extends OrderException {

    private String errorCode;
    private String message;

    public HandoverCarOrderException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public HandoverCarOrderException(DeliveryErrorCode deliveryErrorCode) {
        super(deliveryErrorCode.getName(), deliveryErrorCode.getValue());
        this.errorCode = deliveryErrorCode.getName();
        this.message = deliveryErrorCode.getValue();
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
