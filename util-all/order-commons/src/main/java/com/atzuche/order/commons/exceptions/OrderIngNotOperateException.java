package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @Author ZhangBin
 * @Date 2020/5/25 12:01
 * @Description: 
 * 
 **/
public class OrderIngNotOperateException extends OrderException {

    private final static Logger logger = LoggerFactory.getLogger(OrderNotFoundException.class);

    private String orderNo = null;


    public OrderIngNotOperateException(String orderNo){
        super(ErrorCode.ORDER_ING_NOT_OPERATE_FAIL.getCode(),"订单号{"+orderNo+"}已经开始不允许操作");
        this.orderNo = orderNo;
    }
}
