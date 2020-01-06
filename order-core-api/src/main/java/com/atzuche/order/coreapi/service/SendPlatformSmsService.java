package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ：weixu.chen
 * @date ：Created in 2019/11/8 17:31
 */
@Service
public class SendPlatformSmsService {

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

    public static final int SEND_SRV = 3;

    /** 短信通知  */
    public static int MSG = 3;
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
        paramMap.put("sender", SEND_SRV);
        paramMap.put("type", MSG);
        sendNormalSms(paramMap);
    }

    /**
     * 发送普通短信
     * @param paramMap
     */
    public void sendNormalSms(Map<String, Object> paramMap) {
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        paramMap.put("messageId", messageId);
        rabbitTemplate.convertAndSend(SEND_SMS_NORMAL_CHANNEL_QUEUE, JSON.toJSONString(paramMap));
    }

    /**
     * 发送营销短信
     * @param paramMap
     */
    public void sendMarketSms(Map<String, Object> paramMap) {
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        paramMap.put("messageId", messageId);
        rabbitTemplate.convertAndSend(SEND_SMS_MARKETING_CHANNEL_QUEUE, JSON.toJSONString(paramMap));
    }
}
