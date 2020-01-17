package com.atzuche.order.transport.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.transport.common.TransPortErrorCode;

/**
 * @author 胡春林
 * 租客取还车费用
 */
public class GetReturnCostException extends OrderException {

    private String errorCode;
    private String message;

    public GetReturnCostException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public GetReturnCostException(TransPortErrorCode transPortErrorCode) {
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
