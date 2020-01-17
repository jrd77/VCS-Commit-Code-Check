package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerUpdateFineFailException extends OrderException {
    public OwnerUpdateFineFailException() {
        super(ErrorCode.ADMIN_OWNER_UPDATE_FIEN_FAIL.getCode(), ErrorCode.ADMIN_OWNER_UPDATE_FIEN_FAIL.getText());
    }
}
