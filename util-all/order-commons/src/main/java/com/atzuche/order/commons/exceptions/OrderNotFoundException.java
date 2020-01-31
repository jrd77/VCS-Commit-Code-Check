package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/30 3:23 下午
 **/
public class OrderNotFoundException extends OrderException {
    private static final String errorCode="500009";
    private static final String errorMsg="订单不存在";
    
    private final static Logger logger = LoggerFactory.getLogger(OrderNotFoundException.class);

    private String orderNo = null;
    

    public OrderNotFoundException(String orderNo){
        super(errorCode,"订单号{"+orderNo+"}不存在");
        this.orderNo = orderNo;
    }


}

