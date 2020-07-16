package com.atzuche.order.coreapi.listener;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.entity.mq.WithdrawalsNoticeMq;
import com.atzuche.order.coreapi.service.mq.SecondaryOwnerWithdrawalsNoticeHandleService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 车主提现操作结果通知处理
 *
 * @author pengcheng.fu
 * @date 2020/7/9 15:34
 */
//@Component
//@Slf4j
//@RabbitListener(queues = "auto.send.withdrawals.notice.queue", containerFactory = "rabbitListenerContainerFactory")
//public class SecondaryOwnerWithdrawalsNoticeListener {
//
//    @Autowired
//    private SecondaryOwnerWithdrawalsNoticeHandleService secondaryOwnerWithdrawalsNoticeHandleService;
//
//    @RabbitHandler
//    public void noticeHandle(String message) {
//        log.info("Owner withdrawals notice. context is, message:[{}]", message);
//        Transaction t = Cat.newTransaction(CatConstants.RABBIT_MQ_CALL, "上海银行二清提现操作结果通知处理MQ");
//        try {
//            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "SecondaryOwnerWithdrawalsNoticeListener.noticeHandle");
//            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, message);
//            WithdrawalsNoticeMq noticeMq = JSON.parseObject(message, WithdrawalsNoticeMq.class);
//            secondaryOwnerWithdrawalsNoticeHandleService.process(noticeMq);
//            t.setStatus(Transaction.SUCCESS);
//        } catch (Exception e) {
//            t.setStatus(e);
//            log.error("Owner withdrawals notice. handle err. message:[{}]", message, e);
//            Cat.logError("Owner withdrawals notice. handle err. message:" + message, e);
//        } finally {
//            t.complete();
//        }
//    }
//}
