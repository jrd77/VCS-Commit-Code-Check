package com.atzuche.order.mq.common.base;

import org.springframework.stereotype.Component;

/**
 * @author 胡春林
 * 发送不确认模式 ，不需要继承
 */
@Component
public class BaseProducer extends RabbitMQProducer {


    /**
     * 发送消息
     * @param queueName
     * @param message
     */
    public void sendMessage(String queueName, OrderMessage message)
    {
        super.sendMsg(queueName,message);
    }

    /**
     * 发送消息
     * @param exchange
     * @param routeKey
     * @param message
     */
    public void sendTopicMessage(String exchange,String routeKey,OrderMessage message)
    {
        super.sendTopicMsg(exchange,routeKey,message);
    }

    @Override
    public Boolean ackSuccessResolve(String correlationDataId) {

        return true;
    }
}
