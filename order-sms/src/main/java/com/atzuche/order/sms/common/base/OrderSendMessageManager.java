package com.atzuche.order.sms.common.base;

import com.atzuche.order.mq.common.base.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author 胡春林
 * 消息管理器
 */
@Service
@Slf4j
public class OrderSendMessageManager {

    @Autowired
    OrderSendMessageFactory orderSendMessageFactory;

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
}
