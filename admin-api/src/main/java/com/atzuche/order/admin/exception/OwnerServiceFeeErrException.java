package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerServiceFeeErrException extends OrderException {
    public OwnerServiceFeeErrException() {
        super(ErrorCode.OWNER_SERVICE_FEE_ERR.getCode(), ErrorCode.OWNER_SERVICE_FEE_ERR.getText());
    }
}
