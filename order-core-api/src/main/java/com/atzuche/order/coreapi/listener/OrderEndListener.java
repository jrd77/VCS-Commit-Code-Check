package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.DeRunService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.neworder.OrderStatusMq;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * OrderEndListener
 * 订单取消
 *
 * @author shisong
 * @date 2020/1/2
 */
@Component
public class OrderEndListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEndListener.class);

    private static final String ORDER_END_QUEUE = "status.order.end.queue";

    @Resource
    private DeRunService deRunService;

    @RabbitListener(queues = ORDER_END_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String orderEndJson = new String(message.getBody());
        logger.info("OrderEndListener process start param;[{}]", orderEndJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "订单取消告知德润MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderEndListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderEndJson);
            OrderStatusMq orderStatusMq = GsonUtils.convertObj(orderEndJson, OrderStatusMq.class);
            deRunService.changeRentStatus(orderStatusMq.getOrderNo(),0);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("订单取消告知德润MQ 异常,orderEndJson:[{}] , e :[{}]", orderEndJson,e);
            t.setStatus(e);
            Cat.logError("订单取消告知德润MQ 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("OrderEndListener process end " );
    }

}
