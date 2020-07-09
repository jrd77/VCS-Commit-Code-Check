package com.atzuche.order.coreapi.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 车主提现操作结果通知处理
 *
 * @author pengcheng.fu
 * @date 2020/7/9 15:34
 */
@Component
@Slf4j
@RabbitListener(queues = "auto.send.withdrawals.notice.queue", containerFactory = "rabbitListenerContainerFactory")
public class SecondaryOwnerWithdrawalsNoticeListener {

    @RabbitHandler
    public void noticeHandle(String message) {
        log.info("Owner withdrawals notice. context is, message:[{}]", message);

    }
}
