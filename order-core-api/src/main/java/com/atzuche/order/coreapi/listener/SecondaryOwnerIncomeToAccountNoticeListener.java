package com.atzuche.order.coreapi.listener;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.entity.mq.OwnerIncomeAmtNoticeMq;
import com.atzuche.order.coreapi.service.mq.SecondaryOwnerIncomeToAccountNoticeHandleService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 车主收益到账通知处理
 *
 * @author pengcheng.fu
 * @date 2020/7/9 15:38
 */

@Component
@Slf4j
@RabbitListener(queues = "auto.send.owner.income.toAccount.notice.queue", containerFactory = "rabbitListenerContainerFactory")
public class SecondaryOwnerIncomeToAccountNoticeListener {

    @Autowired
    private SecondaryOwnerIncomeToAccountNoticeHandleService secondaryOwnerIncomeToAccountNoticeHandleService;

    @RabbitHandler
    public void noticeHandle(String message) {
        log.info("Owner income to account notice. context is, message:[{}]", message);

        Transaction t = Cat.newTransaction(CatConstants.RABBIT_MQ_CALL, "上海银行二清资金到账通知处理MQ");
        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "SecondaryOwnerIncomeToAccountNoticeListener.noticeHandle");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, message);
            OwnerIncomeAmtNoticeMq noticeMq = JSON.parseObject(message, OwnerIncomeAmtNoticeMq.class);
            secondaryOwnerIncomeToAccountNoticeHandleService.process(noticeMq);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            log.error("Owner income to account notice. handle err. message:[{}]", message, e);
            Cat.logError("Owner income to account notice. handle err. message:" + message, e);
        } finally {
            t.complete();
        }
    }
}
