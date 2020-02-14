package com.atzuche.order.mq.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 短信基础参数参数
 */
@Slf4j
public class SmsParamsMapUtil {


    /**
     * 组装基础数据
     *
     * @param orderNo    订单号
     * @param renterFlag 租客textCode
     * @param ownerFlag  车主textCode
     * @param paramsMap  其他参数
     * @return
     */
    public static Map getParamsMap(String orderNo, String renterFlag, String ownerFlag, Map paramsMap) {

        Map map = Maps.newHashMap();
        if (StringUtils.isBlank(orderNo)) {
            log.info("没有传入订单号，orderNo:[{}]", orderNo);
            return Maps.newHashMap();
        }
        if (StringUtils.isNotBlank(renterFlag)) {
            map.put("renterFlag", renterFlag);
        }
        if (StringUtils.isNotBlank(ownerFlag)) {
            map.put("ownerFlag", ownerFlag);
        }
        if (!CollectionUtils.isEmpty(paramsMap)) {
            map.putAll(paramsMap);
        }
        return map;
    }
}
