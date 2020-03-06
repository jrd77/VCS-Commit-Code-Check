package com.atzuche.order.coreapi.listener.sms;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.coreapi.listener.push.OrderBaseEventService;
import com.atzuche.order.mq.common.sms.MQSendPlatformSmsService;
import com.atzuche.order.mq.common.sms.ShortMessageSendService;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 胡春林
 * 新订单短信基础service
 */
@Slf4j
@Service(value = "smsOrderBaseEventService")
public class SMSOrderBaseEventService  extends OrderBaseEventService{

    @Resource(name = "smsMQSendPlatformSmsService")
    MQSendPlatformSmsService sendPlatformSmsService;
    @Autowired
    ShortMessageSendService shortMessageSendService;

    /**
     * 发送新订单相关短信数据
     * @param smsParamsMap
     * @param phone
     * @param orderEntity
     * @param memberDTO
     * @param goodsDetailDTO
     */
    @Override
    public void sendShortMessageData(String textCode,Map smsParamsMap, String phone, Object orderEntity, Object memberDTO, Object goodsDetailDTO) {
        String renterTextCode = ShortMessageTypeEnum.getSmsTemplate(String.valueOf(textCode));
        if (StringUtils.isBlank(renterTextCode)) {
            log.info("没有找到该textCode对应的消息模版，textCodeFlag：{}", textCode);
            return;
        }
        List<String> smsFieldNames = shortMessageSendService.getSMSTemplateFeild(renterTextCode);
        if (CollectionUtils.isEmpty(smsFieldNames)) {
            //没有参数 直接发。。
            sendPlatformSmsService.orderPaySms(textCode, phone, "发送新订单短信,手机号:" + phone, null);
            return;
        }
        Map smsTemplateFieldValuesMap = shortMessageSendService.getSmsTemplateMap(smsParamsMap, smsFieldNames, orderEntity, memberDTO, goodsDetailDTO);
        log.info("短信：textCode：[{}],待发送参数：[{}]",textCode, JSONObject.toJSONString(smsTemplateFieldValuesMap));
        sendPlatformSmsService.orderPaySms(textCode, phone, "发送新订单短信,手机号:" + phone, smsTemplateFieldValuesMap);
    }
}
