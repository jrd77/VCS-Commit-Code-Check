package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.renterwz.service.TransIllegalMqService;
import com.atzuche.order.renterwz.service.TransIllegalSendAliYunMq;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * WzFeedbackIllegalListener
 * 仁云流程系统同步订单违章处理方信息MQ
 * @author shisong
 * @date 2019/12/28
 */
@Component
public class WzFeedbackIllegalListener extends RenYunListener{

    private static final Logger logger = LoggerFactory.getLogger(WzFeedbackIllegalListener.class);

    private static final String ORDER_CENTER_WZ_FEED_BACK_QUEUE = "order.center.wz.feedback.queue";

    @Resource
    private TransIllegalMqService transIllegalMqService;

    @Resource
    private TransIllegalSendAliYunMq transIllegalSendAliYunMq;

    @RabbitListener(queues = ORDER_CENTER_WZ_FEED_BACK_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String wzFeedbackIllegalJson = new String(message.getBody());
        logger.info("WzFeedbackIllegalListener process start param;[{}]", wzFeedbackIllegalJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "仁云流程系统同步订单违章处理方信息MQ");

        Map<String,String> resMap = new HashMap<>(16);
        String json = super.processRenYunMessage(resMap , wzFeedbackIllegalJson);

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"WzFeedbackIllegalListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,wzFeedbackIllegalJson);
            transIllegalMqService.renYunFeedbackIllegalHandle(json);
            t.setStatus(Transaction.SUCCESS);
            resMap.put("resCode","00");
            resMap.put("resMsg","mq处理成功");
        } catch (Exception e) {
            logger.error("仁云流程系统同步订单违章处理方信息MQ 异常,wzFeedbackIllegalJson:[{}] , e :[{}]", wzFeedbackIllegalJson,e);
            t.setStatus(e);
            Cat.logError("仁云流程系统同步订单违章处理方信息MQ 异常, e {}", e);
            resMap.put("resCode","03");
            resMap.put("resMsg","mq处理失败：{}"+e.getMessage());
        }finally {
            t.complete();
            transIllegalSendAliYunMq.renYunReceiveQueueResultFeedbackQueue(resMap);
        }
        logger.info("WzFeedbackIllegalListener process end " );
    }

}
