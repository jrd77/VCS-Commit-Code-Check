package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 2:37 下午
 **/
public class LongRentTimeException extends OrderException {
    private final static String ERROR_CODE="500004";
    private final static String ERROR_TEXT="长租订单租期要求：";

    public LongRentTimeException(int minRentDay,int maxRentDay){
        super(ERROR_CODE,ERROR_TEXT + "最短："+minRentDay+"天","最长："+maxRentDay+"天" );
    }
}
