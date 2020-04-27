package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class LongRentTagCheckException extends OrderException {
    private static final String ERROR_CODE="5000085";
    private static final String ERROR_TXT="非长租车辆，不允许下长租订单";

    public LongRentTagCheckException(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
