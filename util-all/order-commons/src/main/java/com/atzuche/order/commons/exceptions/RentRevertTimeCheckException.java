package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class RentRevertTimeCheckException extends OrderException {
    private static final String ERROR_CODE="500008";
    private static final String ERROR_TXT="还车时间”应晚于“起租时间";

    public RentRevertTimeCheckException(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
