package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:09 下午
 **/
public class OwnerOrderNotFoundException extends OrderException {
    private static final String errorCode="700124";
    private static final String errorMsg="有效子订单异常";



    public OwnerOrderNotFoundException(String ownerOrderNo){
        super(errorCode,"车主子订单号{"+ownerOrderNo+"}不存在");
    }
}
