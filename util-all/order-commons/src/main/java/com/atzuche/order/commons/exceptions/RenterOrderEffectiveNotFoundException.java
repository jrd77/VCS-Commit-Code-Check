package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

public class RenterOrderEffectiveNotFoundException extends OrderException {
    private static final String errorCode="700124";
    private static final String errorMsg="有效子订单异常";



    public RenterOrderEffectiveNotFoundException(String orderNo){
        super(errorCode,"主订单号{"+orderNo+"}找不到有效的子订单");
    }
}
