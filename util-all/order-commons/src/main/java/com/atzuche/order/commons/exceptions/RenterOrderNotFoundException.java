package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 租客子订单不存在异常
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:06 下午
 **/
public class RenterOrderNotFoundException extends OrderException {
    private static final String errorCode="700124";
    private static final String errorMsg="有效子订单异常";



    public RenterOrderNotFoundException(String renterOrderNo){
        super(errorCode,"租客子订单号{"+renterOrderNo+"}不存在");
    }
}
