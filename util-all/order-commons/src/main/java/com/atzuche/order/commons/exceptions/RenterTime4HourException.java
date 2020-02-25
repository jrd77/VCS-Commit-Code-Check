package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class RenterTime4HourException extends OrderException {
    private static final String ERROR_CODE="903001";
    private static final String ERROR_TXT="租期必须大于4小时方可使用取还车服务";

    public RenterTime4HourException(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
