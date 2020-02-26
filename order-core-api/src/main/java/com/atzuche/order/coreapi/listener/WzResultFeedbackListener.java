package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.TransIllegalMqService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WzResultFeedbackListener
 * 对方MQ处理结果反馈MQ
 * @author shisong
 * @date 2019/12/28
 */
@Component
public class WzResultFeedbackListener{

    private static final Logger logger = LoggerFactory.getLogger(WzResultFeedbackListener.class);

    private static final String ORDER_CENTER_WZ_RESULT_FEEDBACK_QUEUE = "order.center.wz.result.feedback.queue";

    @Resource
    private TransIllegalMqService transIllegalMqService;

    @RabbitListener(queues = ORDER_CENTER_WZ_RESULT_FEEDBACK_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String wzResultFeedbackJson = new String(message.getBody());
        logger.info("WzResultFeedbackListener process start param;[{}]", wzResultFeedbackJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "对方MQ处理结果反馈MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"WzResultFeedbackListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,wzResultFeedbackJson);
            transIllegalMqService.autoReceiveQueueResultFeedbackQueueHandle(wzResultFeedbackJson);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("对方MQ处理结果反馈MQ 异常,wzResultFeedbackJson:[{}] , e :[{}]", wzResultFeedbackJson,e);
            t.setStatus(e);
            Cat.logError("对方MQ处理结果反馈MQ 异常, e {}", e);
        }finally {
            t.complete();
        }
        logger.info("WzResultFeedbackListener process end " );
    }

}
