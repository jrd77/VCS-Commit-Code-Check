package com.atzuche.order.coreapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * WeChatTemplateService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class WeChatTemplateService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${mq.wechat.push.template.routingkey}")
    private String routingKey;

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void weChatPushTemplateParamerMQSend(String msg) {
        logger.info("微信模板推送发送mq队列：{} 参数：{}", routingKey, msg);
        rabbitTemplate.convertAndSend(routingKey, msg);
    }
}
