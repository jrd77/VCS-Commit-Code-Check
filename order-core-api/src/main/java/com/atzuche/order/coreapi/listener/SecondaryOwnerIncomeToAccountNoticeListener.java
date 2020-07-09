package com.atzuche.order.coreapi.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @RabbitHandler
    public void noticeHandle(String message) {
        log.info("Owner income to account notice. context is, message:[{}]", message);

    }
}
