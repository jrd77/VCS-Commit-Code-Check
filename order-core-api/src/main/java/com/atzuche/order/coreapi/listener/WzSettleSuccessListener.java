package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.renterwz.entity.DerenCarApproachCitysEntity;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * WzSettleSuccessListener
 * 违章结算成功 监听的mq
 *
 * @author shisong
 * @date 2020/1/9
 */
@Component
public class WzSettleSuccessListener {

    private static final Logger logger = LoggerFactory.getLogger(WzSettleSuccessListener.class);




    public void process(Message message) {
        String orderNo = new String(message.getBody());
        logger.info("orderNo process start param;[{}]", orderNo);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "监听违章结算MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"WzSettleSuccessListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderNo);
            //TODO
            //deRenCarApproachCitiesService.save(orderNo);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("监听违章结算MQ 异常,orderNo:[{}] , e :[{}]", orderNo,e);
            t.setStatus(e);
            Cat.logError("监听违章结算MQ 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("WzSettleSuccessListener process end " );
    }

}
