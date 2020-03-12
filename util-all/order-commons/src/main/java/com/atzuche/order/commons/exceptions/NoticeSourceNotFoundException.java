package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class NoticeSourceNotFoundException extends OrderException {
    public NoticeSourceNotFoundException() {
        super(ErrorCode.NOTICE_SOURCE_ERR.getCode(), ErrorCode.NOTICE_SOURCE_ERR.getText());
    }
}
