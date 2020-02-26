package com.atzuche.order.mq.common.base;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author 胡春林
 * @desc 消息队列服务发送接口
 */
@Component
@Slf4j
public abstract class RabbitMQProducer implements RabbitTemplate.ConfirmCallback {

    @Resource(name = "orderSmsRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到队列
     * @param queueName 队列名称
     * @param message   消息内容
     */
    public void sendMsg(String queueName, OrderMessage message) {

        String correlationDataId = UUID.randomUUID().toString();
        log.info("messageId is " + correlationDataId + ",send message is" + JSONObject.toJSONString(message));
        if (message == null) {
            log.error("messageId is " + correlationDataId + ",send message failed: message is null");
        }
        rabbitTemplate.setConfirmCallback(this);
        CorrelationData correlationData = new CorrelationData(correlationDataId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DEFAULT_EXCHANGE, queueName, message.getMessage(), correlationData);
    }

    /**
     * 发送消息到topic
     * @param message
     */
    public void sendTopicMsg(String exchange,String routeKey, OrderMessage message) {

        String correlationDataId = UUID.randomUUID().toString();
        log.info("messageId is " + correlationDataId + ",send message is" + JSONObject.toJSONString(message));
        if (message == null) {
            log.error("messageId is " + correlationDataId + ",send message failed: message is null");
        }
        rabbitTemplate.setConfirmCallback(this);
        CorrelationData correlationData = new CorrelationData(correlationDataId);
        rabbitTemplate.convertAndSend(exchange,routeKey,message,correlationData);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return rabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("messageId is " + correlationData.getId() + ",send message success");
            ackSuccessResolve(correlationData.getId());
        } else {
            log.error("messageId is " + correlationData.getId() + ",send message failed:" + cause);
        }
    }

    /**
     * 确认成功
     * @param correlationDataId
     * @return
     */
    public abstract Boolean ackSuccessResolve(String correlationDataId);

}
