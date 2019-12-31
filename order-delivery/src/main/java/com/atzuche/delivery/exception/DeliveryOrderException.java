package com.atzuche.delivery.exception;


import com.atzuche.delivery.common.DeliveryErrorCode;
import com.atzuche.order.commons.OrderException;

/**
 * 业务异常
 *
 * @author 胡春林
 **/
public class DeliveryOrderException extends OrderException {

    private String errorCode;
    private String message;

    public DeliveryOrderException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public DeliveryOrderException(DeliveryErrorCode deliveryErrorCode) {
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
