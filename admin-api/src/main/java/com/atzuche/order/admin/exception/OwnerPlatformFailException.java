package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerPlatformFailException extends OrderException {
    public OwnerPlatformFailException() {
        super(ErrorCode.OWNER_PLATFORM_FAIL.getCode(), ErrorCode.OWNER_PLATFORM_FAIL.getText());
    }
}
