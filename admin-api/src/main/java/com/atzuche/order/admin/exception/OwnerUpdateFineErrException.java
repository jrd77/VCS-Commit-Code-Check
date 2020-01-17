package com.atzuche.order.admin.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerUpdateFineErrException extends OrderException {
    public OwnerUpdateFineErrException() {
        super(ErrorCode.ADMIN_OWNER_UPDATE_FIEN_ERR.getCode(), ErrorCode.ADMIN_OWNER_UPDATE_FIEN_ERR.getText());
    }
}
