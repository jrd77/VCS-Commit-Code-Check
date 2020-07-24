package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;


public class LianHeMaiTongMemberException extends OrderException {

    public LianHeMaiTongMemberException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
