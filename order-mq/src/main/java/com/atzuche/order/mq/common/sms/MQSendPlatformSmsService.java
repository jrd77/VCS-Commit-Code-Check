package com.atzuche.order.mq.common.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ：weixu.chen
 * @date ：Created in 2019/11/8 17:31
 */
@Service
public class MQSendPlatformSmsService {

    private static final Logger logger = LoggerFactory.getLogger(MQSendPlatformSmsService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 普通短信队列
     */
    private final static String SEND_SMS_NORMAL_CHANNEL_QUEUE = "auto_send_sms_normal_channel_queue";

    /**
     * 营销短信队列
     */
    private final static String SEND_SMS_MARKETING_CHANNEL_QUEUE = "auto_send_sms_marketing_channel_queue";

    /**
     * 自动调度配车成功后发送短信
     * @param textCode 模板code
     * @param mobile 手机号
     * @param message 备注
     * @param orderNo 订单号
     * @param packageName 套餐名称
     * @param brand 车品牌
     * @param type 车型号
     */
    public void carMatchSuccessSms(String textCode, String mobile, String message, Long orderNo, String packageName, String brand, String type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("textCode", textCode);
        paramMap.put("mobile", mobile);
        paramMap.put("message", message);
        paramMap.put("orderNo", orderNo);
        paramMap.put("packageName", packageName);
        paramMap.put("brandTxt", brand);
        paramMap.put("typeTxt", type);
        paramMap.put("sender", SMS.SEND_SRV);
        paramMap.put("type", SMS.MSG);
        sendNormalSms(paramMap);
    }

    /**
     * 支付成功后发送短信
     * @param textCode
     * @param mobile
     * @param message
     * @param telephone
     */
    public void orderPaySuccessSms(String textCode, String mobile, String message, String telephone) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("textCode", textCode);
        paramMap.put("mobile", mobile);
        paramMap.put("message", message);
        paramMap.put("telephone", telephone);
        paramMap.put("sender", SMS.SEND_SRV);
        paramMap.put("type", SMS.MSG);
        sendNormalSms(paramMap);
    }


    /**
     * 订单发送短信(通用版 只传配置好的参数  订单支付成功 退款)
     * @param textCode
     * @param mobile
     * @param message
     */
    public void orderPaySms(String textCode, String mobile, String message, Map map) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("textCode", textCode);
        paramMap.put("mobile", mobile);
        paramMap.put("message", message);
        paramMap.put("sender", SMS.SEND_SRV);
        paramMap.put("type", SMS.MSG);
        if (MapUtils.isNotEmpty(map)) {
            Set<String> msgDataFieldKey = map.keySet();
            Iterator<String> iterable = msgDataFieldKey.iterator();
            while (iterable.hasNext()) {
                String fieldKey = iterable.next();
                paramMap.put(fieldKey, map.get(fieldKey));
            }
        }
        sendNormalSms(paramMap);
    }



    /**
     * 发送普通短信
     * @param paramMap
     */
    public void sendNormalSms(Map<String, Object> paramMap) {
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        paramMap.put("messageId", messageId);
        logger.info("Send normal sms.param is,paramMap:[{}]", JSONObject.toJSONString(paramMap));
        rabbitTemplate.convertAndSend(SEND_SMS_NORMAL_CHANNEL_QUEUE, JSON.toJSONString(paramMap));
    }

    /**
     * 发送营销短信
     * @param paramMap
     */
    public void sendMarketSms(Map<String, Object> paramMap) {
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        paramMap.put("messageId", messageId);
        logger.info("Send market sms.param is,paramMap:[{}]", JSONObject.toJSONString(paramMap));
        rabbitTemplate.convertAndSend(SEND_SMS_MARKETING_CHANNEL_QUEUE, JSON.toJSONString(paramMap));
    }
}
