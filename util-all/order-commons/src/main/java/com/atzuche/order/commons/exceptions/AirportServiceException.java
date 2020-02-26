package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class AirportServiceException extends OrderException {
    private static final String ERROR_CODE="500139";
    private static final String ERROR_TXT="取车地址不是凹凸机场服务点，无法使用机场送车服务！";

    public AirportServiceException() {
        super(ERROR_CODE, ERROR_TXT);
    }
}
