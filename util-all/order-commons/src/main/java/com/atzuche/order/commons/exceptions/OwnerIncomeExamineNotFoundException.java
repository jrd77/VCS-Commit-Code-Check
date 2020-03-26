package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerIncomeExamineNotFoundException extends OrderException {
    public OwnerIncomeExamineNotFoundException() {
        super(ErrorCode.OWNER_INCOM_EXAM_ERR.getCode(), ErrorCode.OWNER_INCOM_EXAM_ERR.getText());
    }
}
