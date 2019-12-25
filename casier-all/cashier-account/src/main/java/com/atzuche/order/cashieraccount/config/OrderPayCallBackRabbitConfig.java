package com.atzuche.order.cashieraccount.config;

import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RabbitBusinessTypeEnum;
import com.atzuche.order.commons.service.RabbitMsgLogService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 支付系统回调
 */
@Component
@Slf4j
public class OrderPayCallBackRabbitConfig {
    @Autowired RabbitMsgLogService rabbitMsgLogService;
    @Autowired CashierPayService cashierPayService;
    /**
     * 支付系统回调
     * MQ 异步回调
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${auto-coin-recharge}",durable = "true"),
            exchange = @Exchange(value="${auto-coin-recharge}",durable = "true"),
            key = "${auto-coin-recharge}"
    )
    )
    @RabbitHandler
    public void payCallBackAsyn(Message message, Channel channel){
        log.info("OrderPayCallBack payCallBackAsyn start param;[{}]", message);
        try {
            String orderPayAsynStr = new String(message.getBody());
            rabbitMsgLogService.insertRabbitMsgLog(message,RabbitBusinessTypeEnum.ORDER_PAY_CALL_BACK,orderPayAsynStr);
            cashierPayService.payCallBackAsyn(orderPayAsynStr);
        } catch (Exception e) {
            log.error("OrderPayCallBack payCallBackAsyn,e={},message={}",e,message);
        }
        log.info("OrderPayCallBack payCallBackAsyn end " );
    }


}
