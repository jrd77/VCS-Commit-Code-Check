package com.atzuche.order.mem;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class RenterDriverFailException extends OrderException {

    public RenterDriverFailException() {
        super(ErrorCode.FEIGN_MEMBER_DRIVER_fAIL.getCode(), ErrorCode.FEIGN_MEMBER_DRIVER_fAIL.getText());
    }
}
