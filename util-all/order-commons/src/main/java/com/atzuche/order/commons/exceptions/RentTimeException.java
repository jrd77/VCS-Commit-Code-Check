package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 2:37 下午
 **/
public class RentTimeException extends OrderException {
    private final static String ERROR_CODE="500004";
    private final static String ERROR_TEXT="订单租期要求：";

    public RentTimeException(String msg){
        super(ERROR_CODE,ERROR_TEXT + msg);
    }
}
