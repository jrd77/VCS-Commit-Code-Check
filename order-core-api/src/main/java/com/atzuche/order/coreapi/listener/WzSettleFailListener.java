package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.renterwz.service.RenterOrderWzSettleFlagService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.neworder.OrderWzSettlementMq;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WzSettleSuccessListener
 *
 * @author shisong
 * @date 2020/2/19
 */
@Component
public class WzSettleFailListener {

    private static final Logger logger = LoggerFactory.getLogger(WzSettleFailListener.class);

    private static final String ORDER_WZ_SETTLEMENT_FAIL_QUEUE = "action.order.wz.settlement.fail.queue";

    @Resource
    private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;

    @RabbitListener(queues = ORDER_WZ_SETTLEMENT_FAIL_QUEUE, containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String orderWzSettleFailJson = new String(message.getBody());
        logger.info("WzSettleFailListener process start param;[{}]", orderWzSettleFailJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "违章结算失败mq");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"WzSettleFailListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderWzSettleFailJson);
            OrderMessage orderMessage = GsonUtils.convertObj(orderWzSettleFailJson, OrderMessage.class);
            OrderWzSettlementMq orderWzSettlementMq = (OrderWzSettlementMq)orderMessage.getMessage();
            renterOrderWzSettleFlagService.updateSettle(orderWzSettlementMq.getOrderNo(),0);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("违章结算失败mq 异常,orderWzSettleFailJson:[{}] , e :[{}]", orderWzSettleFailJson,e);
            t.setStatus(e);
            Cat.logError("违章结算失败mq 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("WzSettleFailListener process end " );
    }

}
