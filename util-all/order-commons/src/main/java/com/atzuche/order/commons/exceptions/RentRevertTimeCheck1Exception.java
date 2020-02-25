package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class RentRevertTimeCheck1Exception extends OrderException {
    private static final String ERROR_CODE="500084";
    private static final String ERROR_TXT="取车时间不可超过下下个月最后一天";

    public RentRevertTimeCheck1Exception(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
