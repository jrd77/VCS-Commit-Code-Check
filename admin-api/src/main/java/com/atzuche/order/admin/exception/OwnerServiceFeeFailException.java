package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerServiceFeeFailException extends OrderException {
    public OwnerServiceFeeFailException() {
        super(ErrorCode.OWNER_SERVICE_FEE_FAIL.getCode(), ErrorCode.OWNER_SERVICE_FEE_FAIL.getText());
    }
}
