package com.atzuche.order.mq.common.sms;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.enums.PushParamsTypeEnum;
import com.autoyol.commons.utils.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 胡春林
 * 发送短信数据
 */
@Service
@Slf4j
public class ShortMessageSendService {

    private String reg = ("\\$(.+?)\\$");

    /**
     * 获取需要发送的SMS Map
     * @param smsParamsMap   参数
     * @param smsFieldNames  需要发送的参数字段名
     * @param orderEntity    订单对象
     * @param memberDTO      会员对象
     * @param goodsDetailDTO 商品对象
     * @return
     */
    public Map getSmsTemplateMap(Map smsParamsMap, List<String> smsFieldNames, Object orderEntity, Object memberDTO, Object goodsDetailDTO) {
        Map smsTemplateFieldValus = Maps.newHashMap();
        for (String smsFieldName : smsFieldNames) {
            String smsFieldValue = getFieldValueByFieldName(smsFieldName, orderEntity) == null ? getFieldValueByFieldName(smsFieldName, memberDTO) : getFieldValueByFieldName(smsFieldName, orderEntity);
            smsFieldValue = smsFieldValue == null ? getFieldValueByFieldName(smsFieldName, goodsDetailDTO) : smsFieldValue;
            //如果都为空 继续往下找
            if (StringUtils.isBlank(smsFieldValue)) {
                smsFieldValue = String.valueOf(smsParamsMap.get(smsFieldName));
            }
            //如果任然为空 说明没传这个字段
            if (StringUtils.isBlank(smsFieldValue)) {
                continue;
            }
            smsTemplateFieldValus.put(smsFieldName, smsFieldValue);
        }
        return smsTemplateFieldValus;
    }

    /**
     * 根据属性名获取属性值
     * @param fieldName
     * @param object
     * @return
     */
    public String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
//            if (LocalDateTime.class.isInstance(field.get(object))) {
//                LocalDateTime localDateTime = (LocalDateTime) field.get(object);
//                return DateUtils.formate(localDateTime, DateUtils.DATE_DEFAUTE1);
//            } else {
//                return String.valueOf(field.get(object));
//            }
            return String.valueOf(field.get(object));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取需要取值得字段名
     * @param smsTemplate
     * @return
     */
    public List<String> getSMSTemplateFeild(String smsTemplate) {
        Pattern patten = Pattern.compile(reg);
        Matcher matcher = patten.matcher(smsTemplate);
        List<String> matchFeilds = Lists.newArrayList();
        while (matcher.find()) {
            matchFeilds.add(matcher.group(1));
        }
        return matchFeilds;
    }

    /**
     * 还原push所需得参数key
     * @param pushParamsMap
     * @return
     */
    public Map replacePushParamsMapKey(Map pushParamsMap) {
        if (MapUtils.isNotEmpty(pushParamsMap)) {
            Map map = Maps.newHashMap();
            Set<String> msgDataFieldKey = pushParamsMap.keySet();
            Iterator<String> iterable = msgDataFieldKey.iterator();
            while (iterable.hasNext()) {
                String fieldKey = iterable.next();
                if (StringUtils.isNotBlank(PushParamsTypeEnum.getFeildName(fieldKey))) {
                    map.put(PushParamsTypeEnum.getFeildName(fieldKey), pushParamsMap.get(fieldKey));
                }
            }
            return map;
        }
        return null;
    }





}