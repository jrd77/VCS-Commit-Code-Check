package com.atzuche.order.sms.common.base;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.sms.common.OrderMessageServiceScanner;
import com.atzuche.order.sms.enums.MessageServiceTypeEnum;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;
import com.autoyol.commons.utils.StringUtils;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author 胡春林
 * 消息管理器
 */
@Service
@Slf4j
public class OrderSendMessageManager {

    @Autowired
    OrderSendMessageFactory orderSendMessageFactory;
    @Autowired
    OrderMessageServiceScanner orderMessageServiceScanner;

    /**
     * 发送sms
     * @param orderMessage
     */
    public void sendSMSMessageData(OrderMessage orderMessage) {
        Map smsParamsMap = orderMessage.getMap();
        if (CollectionUtils.isEmpty(smsParamsMap)) {
            log.info("没有短信需要发送--->>>>orderMessage:[{}]", orderMessage.toString());
            return;
        }
        if (!smsParamsMap.containsKey("orderNo")) {
            log.info("缺少短信需要发送的订单号参数--->>>>orderMessage:[{}]", orderMessage.toString());
            return;
        }
        orderSendMessageFactory.sendShortMessage(smsParamsMap);
    }

    /**
     * 发送push
     * @param orderMessage
     */
    public void sendPushMessageData(OrderMessage orderMessage) {
        Map pushParamsMap = orderMessage.getPushMap();
        if (CollectionUtils.isEmpty(pushParamsMap)) {
            log.info("没有push、通知需要发送--->>>>orderMessage:[{}]", orderMessage.toString());
            return;
        }
        if (!pushParamsMap.containsKey("orderNo")) {
            log.info("缺少push、通知需要发送的订单号参数--->>>>orderMessage:[{}]", orderMessage.toString());
            return;
        }
        orderSendMessageFactory.sendPushMessage(pushParamsMap);
    }


    /**
     * 获取对应得service
     * @param message
     * @return
     */
    public OrderMessage createOrderMessageService(Message message){
        OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
        log.info("新订单动作总事件监听,入参orderMessage:[{}]", orderMessage.toString());
        String routeKeyName = message.getMessageProperties().getReceivedRoutingKey();
        String serviceName = MessageServiceTypeEnum.getSmsServiceTemplate(routeKeyName);
        if(StringUtils.isBlank(serviceName))
        {
            log.info("该事件没有需要发送得短信,routeKeyName:[{}]",routeKeyName);
            return orderMessage;
        }
        serviceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
        IOrderRouteKeyMessage orderRouteKeyMessage = orderMessageServiceScanner.getBean(serviceName);
        if(Objects.isNull(orderRouteKeyMessage))
        {
            log.info("该事件没有对应的短信服务,routeKeyName:[{}]",routeKeyName);
            return orderMessage;
        }
        orderMessage = orderRouteKeyMessage.sendOrderMessageWithNo(orderMessage);
        return orderMessage;
    }

}
