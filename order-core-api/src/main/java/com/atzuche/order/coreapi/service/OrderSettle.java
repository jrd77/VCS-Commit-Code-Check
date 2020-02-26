package com.atzuche.order.coreapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.service.OrderWzSettleService;

/***
 * 处理相互依赖问题
 */
@Service
public class OrderSettle {
    @Autowired OrderSettleService orderSettleService;
    @Autowired PayCallbackService payCallbackService;
    
    @Autowired
    OrderWzSettleService orderWzSettleService;
    
    /**
     * order-core-api 入口 车辆押金结算
     * @param orderNo
     */
    public void settleOrder(String orderNo){
        orderSettleService.settleOrder(orderNo,payCallbackService);
    }
    
    public void settleWzOrder(String orderNo){
    	orderWzSettleService.settleWzOrder(orderNo);
    }
    
}
