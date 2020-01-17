package com.atzuche.order.mq.listener;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.util.MQSendPlatformSmsService;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 胡春林
 * 订单的总status事件处理
 */
@Component
@Slf4j
public class OrderStatusEventListener {

    @Autowired
    MQSendPlatformSmsService sendPlatformSmsService;

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "order_status_02", durable = "true"),
            exchange = @Exchange(value = "auto-order-status", durable = "true", type = "topic"), key = "status.#")
    },containerFactory = "rabbitListenerContainerFactory")
    public void process(Message message) {
        log.info("receive order status message: " + new String(message.getBody()));
        OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
        log.info("新订单状态总事件监听,入参orderMessage:[{}]", orderMessage.toString());
        try {
            sendPlatformSmsService.orderPaySms(orderMessage.getContext(), orderMessage.getPhone(), "大事件備注", null);
            log.info("新订单status事件成功发送短信,----------->>>>>>内容：{},手机号:{}", orderMessage.getContext(), orderMessage.getPhone());
        } catch (Exception e) {
            log.info("新订单status事件发生异常,msg：[{}]", e.getMessage());
            Cat.logError("新订单status事件发生异常", e);
        }
    }
}
