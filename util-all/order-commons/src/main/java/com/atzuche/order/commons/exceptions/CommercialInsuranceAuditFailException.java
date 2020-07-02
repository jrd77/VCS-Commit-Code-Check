package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class CommercialInsuranceAuditFailException extends OrderException {

    public CommercialInsuranceAuditFailException() {
        super(ErrorCode.COMMERCIAL_INSURANCE_AUDIT_FAIL.getCode(), ErrorCode.COMMERCIAL_INSURANCE_AUDIT_FAIL.getText());
    }
}
