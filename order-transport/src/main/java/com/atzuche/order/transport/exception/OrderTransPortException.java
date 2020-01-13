package com.atzuche.order.transport.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.transport.common.TransPortErrorCode;

/**
 * 业务异常
 * @author 胡春林
 **/
public class OrderTransPortException extends OrderException {

    private String errorCode;
    private String message;

    public OrderTransPortException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public OrderTransPortException(TransPortErrorCode transPortErrorCode) {
        super(transPortErrorCode.getName(), transPortErrorCode.getValue());
        this.errorCode = transPortErrorCode.getName();
        this.message = transPortErrorCode.getValue();
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
