package com.atzuche.order.commons.service;

import com.atzuche.order.commons.entity.RabbitMsgLogEntity;
import com.atzuche.order.commons.enums.RabbitBusinessTypeEnum;
import com.atzuche.order.commons.mapper.RabbitMsgLogMapper;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * rabbitmq接收消息记录
 *
 * @author ZhangBin
 * @date 2019-12-23 19:27:14
 */
@Service
public class RabbitMsgLogService{
    @Autowired
    private RabbitMsgLogMapper rabbitMsgLogMapper;


    public RabbitMsgLogEntity insertRabbitMsgLog(Message message, RabbitBusinessTypeEnum orderPayCallBack, String toJson) {
        RabbitMsgLogEntity rabbitMsgLog = new RabbitMsgLogEntity();
        String exchange = message.getMessageProperties().getReceivedExchange();
        String queue = message.getMessageProperties().getConsumerQueue();
        String mqKey = message.getMessageProperties().getMessageId();

        rabbitMsgLog.setBusinessType(orderPayCallBack.getCode());
        rabbitMsgLog.setCreateTime(LocalDateTime.now());
        rabbitMsgLog.setIsConsume(NumberUtils.INTEGER_ZERO);
        rabbitMsgLog.setMqExchange(exchange);
        rabbitMsgLog.setMqKey(mqKey);
        rabbitMsgLog.setMqQueue(queue);
        rabbitMsgLog.setMqMsg(toJson);
        rabbitMsgLog.setUniqueId(NumberUtils.INTEGER_ZERO);
        rabbitMsgLogMapper.insert(rabbitMsgLog);
        return rabbitMsgLog;

    }
}
