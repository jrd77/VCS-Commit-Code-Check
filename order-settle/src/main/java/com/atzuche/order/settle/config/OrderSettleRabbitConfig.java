package com.atzuche.order.settle.config;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RabbitBusinessTypeEnum;
import com.atzuche.order.commons.service.RabbitMsgLogService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 车辆结算MQ事件
 */
@Component
@Slf4j
public class OrderSettleRabbitConfig {
    @Autowired RabbitMsgLogService rabbitMsgLogService;
    @Autowired OrderSettleService orderSettleService;
    /**
     * 车辆结算MQ事件
     */
    @RabbitListener(queues="xxx")
    @RabbitHandler
    public void settleCallBack(Message message, Channel channel,String orderNo){
        log.info("OrderSettleRabbitConfig settleCallBack start param;[{}]", message);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBack");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderSettleRabbitConfig.settleCallBack");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderNo);
            rabbitMsgLogService.insertRabbitMsgLog(message, RabbitBusinessTypeEnum.OEDER_SETTLE,orderNo,orderNo);
            orderSettleService.settleOrder(orderNo);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderSettleRabbitConfig settleCallBack,e={},message={}",e,message);
            t.setStatus(e);
            Cat.logError("车辆结算MQ事件 失败,e {}",e);
        } finally {
            t.complete();
        }
        log.info("OrderSettleRabbitConfig settleCallBack end " );
    }


}
