package com.atzuche.order.coreapi.listener.push;

import com.atzuche.order.coreapi.listener.sms.SMSOrderBaseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 胡春林
 * 单纯发送短信、push、消息
 */
@Service
public class OrderSendMessageFactory {

    @Autowired
    PushOrderBaseEventService pushOrderBaseEventService;
    @Autowired
    SMSOrderBaseEventService smsOrderBaseEventService;

    /**
     * 发送短信数据
     * @param smsParamsMap
     */
    public void sendShortMessage(Map smsParamsMap) {
        smsOrderBaseEventService.sendShortMessage(smsParamsMap);
    }

    /**
     * 发送push、消息数据
     * @param pushParamsMap
     */
    public void sendPushMessage(Map pushParamsMap) { pushOrderBaseEventService.sendShortMessage(pushParamsMap); }





}
