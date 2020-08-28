package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class PriceDayGroupException extends OrderException {
    private static final String ERROR_CODE="500239";
    private static final String ERROR_TXT="获取价格分组失败！";

    public PriceDayGroupException() {
        super(ERROR_CODE, ERROR_TXT);
    }
}
