package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OwnerIncomeExamineInsertException extends OrderException {
    public OwnerIncomeExamineInsertException() {
        super(ErrorCode.OWNER_INCOM_EXAM_INSERT_ERR.getCode(), ErrorCode.OWNER_INCOM_EXAM_INSERT_ERR.getText());
    }
}
