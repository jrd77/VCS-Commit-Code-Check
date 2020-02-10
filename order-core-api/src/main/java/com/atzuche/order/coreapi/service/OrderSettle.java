package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.settle.service.OrderSettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * 处理相互依赖问题
 */
@Service
public class OrderSettle {
    @Autowired OrderSettleService orderSettleService;
    @Autowired PayCallbackService payCallbackService;

    public void settleOrder(String orderNo){
        orderSettleService.settleOrder(orderNo,payCallbackService);

    }
}
