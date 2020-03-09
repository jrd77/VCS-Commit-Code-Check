package com.atzuche.order.coreapi.listener.push;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.mq.common.sms.ShortMessageSendService;
import com.atzuche.order.mq.enums.MessageTypeEnum;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.enums.PushParamsTypeEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 胡春林
 * 发送push消息基础service
 */
@Slf4j
@Service(value = "pushOrderBaseEventService")
public class PushOrderBaseEventService extends OrderBaseEventService {

    @Autowired
    PushMessageService pushMessageService;
    @Autowired
    ShortMessageSendService shortMessageSendService;

    @Override
    public void sendShortMessageData(String textCode, Map smsParamsMap, String phone, Object orderEntity, Object memberDTO, Object goodsDetailDTO) {
        String renterTextCode = PushMessageTypeEnum.getSmsTemplate(String.valueOf(textCode));
        if (StringUtils.isBlank(renterTextCode)) {
            log.info("没有找到该textCode对应的消息模版，textCodeFlag：{}", textCode);
            return;
        }
        //构造原始参数
        Map pushParamsMap = Maps.newHashMap();
        pushParamsMap.put("event", textCode);
        pushParamsMap.put("memNo", shortMessageSendService.getFieldValueByFieldName("memNo", memberDTO));
        pushParamsMap.put("messageType", MessageTypeEnum.ORDER_MESSAGE.getMessageType());
        List<String> smsFieldNames = shortMessageSendService.getSMSTemplateFeild(renterTextCode);
        if (CollectionUtils.isEmpty(smsFieldNames)) {
            //没有参数 直接发。。
            log.info("发送push消息数据----->>>[{}]", JSONObject.toJSONString(pushParamsMap));
            pushMessageService.sendCompleteOrderMsg(pushParamsMap);
            return;
        }
        smsFieldNames = smsFieldNames.stream().map(r -> PushParamsTypeEnum.getFeildValue(r)).collect(Collectors.toList());
        Map smsTemplateFieldValuesMap = shortMessageSendService.getSmsTemplateMap(smsParamsMap, smsFieldNames, orderEntity, memberDTO, goodsDetailDTO);
        if (!CollectionUtils.isEmpty(smsTemplateFieldValuesMap)) {
            smsTemplateFieldValuesMap = shortMessageSendService.replacePushParamsMapKey(smsTemplateFieldValuesMap);
            pushParamsMap.putAll(smsTemplateFieldValuesMap);
        }
        log.info("发送有参数的push消息数据----->>>[{}]", JSONObject.toJSONString(pushParamsMap));
        pushMessageService.sendCompleteOrderMsg(pushParamsMap);
    }
}
