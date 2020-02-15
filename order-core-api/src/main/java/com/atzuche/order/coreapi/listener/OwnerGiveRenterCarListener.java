package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.DeRunService;
import com.atzuche.order.coreapi.service.IllegalToDoService;
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
 * OwnerGiveRenterCarListener
 * 车主交车
 *
 * @author shisong
 * @date 2020/1/2
 */
@Component
public class OwnerGiveRenterCarListener {

    private static final Logger logger = LoggerFactory.getLogger(OwnerGiveRenterCarListener.class);

    private static final String ORDER_PRERETURNCAR_QUEUE = "status.order.preReturnCar.queue";

    @Resource
    private DeRunService deRunService;

    @RabbitListener(queues = ORDER_PRERETURNCAR_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String ownerGiveRenterCarJson = new String(message.getBody());
        logger.info("OwnerGiveRenterCarListener process start param;[{}]", ownerGiveRenterCarJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "车主交车告知德润MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OwnerGiveRenterCarListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,ownerGiveRenterCarJson);
            OrderStatusMq orderStatusMq = GsonUtils.convertObj(ownerGiveRenterCarJson, OrderStatusMq.class);
            deRunService.changeRentStatus(orderStatusMq.getOrderNo(),1);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("车主交车告知德润MQ 异常,ownerGiveRenterCarJson:[{}] , e :[{}]", ownerGiveRenterCarJson,e);
            t.setStatus(e);
            Cat.logError("车主交车告知德润MQ 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("OwnerGiveRenterCarListener process end " );
    }

}
