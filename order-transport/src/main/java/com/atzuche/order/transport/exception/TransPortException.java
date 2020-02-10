package com.atzuche.order.transport.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.transport.common.TransPortErrorCode;

/**
 * 费用业务异常
 *
 * @author 胡春林
 **/
public class TransPortException extends OrderException {

    private String errorCode;
    private String message;

    public TransPortException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public TransPortException(TransPortErrorCode transPortErrorCode) {
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
