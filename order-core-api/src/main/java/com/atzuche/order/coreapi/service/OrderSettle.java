package com.atzuche.order.coreapi.service;

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

    /**
     * order-core-api 入口 车辆押金结算
     * @param orderNo
     */
    public void settleOrder(String orderNo){
        orderSettleService.settleOrder(orderNo,payCallbackService);
    }
}
