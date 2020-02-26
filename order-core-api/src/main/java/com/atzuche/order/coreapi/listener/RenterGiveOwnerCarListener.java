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
 * RenterGiveOwnerCarListener
 * 租客还车
 *
 * @author shisong
 * @date 2020/1/2
 */
@Component
public class RenterGiveOwnerCarListener {

    private static final Logger logger = LoggerFactory.getLogger(RenterGiveOwnerCarListener.class);

    private static final String ORDER_PRESETTLEMENT_QUEUE = "status.order.preSettlement.queue";

    @Resource
    private DeRunService deRunService;

    @RabbitListener(queues = ORDER_PRESETTLEMENT_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String renterGiveOwnerCarJson = new String(message.getBody());
        logger.info("RenterGiveOwnerCarListener process start param;[{}]", renterGiveOwnerCarJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "租客还车告知德润MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"RenterGiveOwnerCarListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,renterGiveOwnerCarJson);
            OrderStatusMq orderStatusMq = GsonUtils.convertObj(renterGiveOwnerCarJson, OrderStatusMq.class);
            deRunService.changeRentStatus(orderStatusMq.getOrderNo(),0);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("租客还车告知德润MQ 异常,renterGiveOwnerCarJson:[{}] , e :[{}]", renterGiveOwnerCarJson,e);
            t.setStatus(e);
            Cat.logError("租客还车告知德润MQ 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("RenterGiveOwnerCarListener process end " );
    }

}
