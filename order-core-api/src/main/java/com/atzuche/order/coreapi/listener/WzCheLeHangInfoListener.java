package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.IllegalToDoService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WzCheLeHangInfoListener
 *
 * @author shisong
 * @date 2020/1/2
 */
@Component
public class WzCheLeHangInfoListener {

    private static final Logger logger = LoggerFactory.getLogger(WzFeedbackIllegalListener.class);

    private static final String ORDER_CENTER_WZ_CHE_LE_HANG_INFO_QUEUE = "order.center.wz.che.le.hang.info.queue";

    @Resource
    private IllegalToDoService illegalToDoService;

    @RabbitListener(queues = ORDER_CENTER_WZ_CHE_LE_HANG_INFO_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String wzCheLeHangInfoJson = new String(message.getBody());
        logger.info("WzCheLeHangInfoListener process start param;[{}]", wzCheLeHangInfoJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "查询车乐行数据反馈订单违章处理信息MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"WzCheLeHangInfoListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,wzCheLeHangInfoJson);
            IllegalToDO illegalToDO = GsonUtils.convertObj(wzCheLeHangInfoJson, IllegalToDO.class);
            illegalToDoService.processCheLeHangInfoViolations(illegalToDO);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("查询车乐行数据反馈订单违章处理信息MQ 异常,wzCheLeHangInfoJson:[{}] , e :[{}]", wzCheLeHangInfoJson,e);
            t.setStatus(e);
            Cat.logError("查询车乐行数据反馈订单违章处理信息MQ 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("WzCheLeHangInfoListener process end " );
    }

}
