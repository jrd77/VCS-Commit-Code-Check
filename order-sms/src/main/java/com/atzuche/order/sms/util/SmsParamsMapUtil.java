package com.atzuche.order.sms.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author 胡春林
 * 短信基础参数参数
 */
@Slf4j
public class SmsParamsMapUtil {


    /**
     * 组装基础数据
     * @param orderNo    订单号
     * @param renterTextCodeFlag 租客textCode
     * @param ownerTextCodeFlag  车主textCode
     * @param paramsMap  其他参数
     * @return
     */
    public static Map getParamsMap(String orderNo, String renterTextCodeFlag, String ownerTextCodeFlag, Map paramsMap) {

        Map map = Maps.newHashMap();
        if (StringUtils.isBlank(orderNo)) {
            log.info("没有传入订单号，orderNo:[{}]", orderNo);
            return Maps.newHashMap();
        }
        map.put("orderNo", orderNo);
        if (StringUtils.isNotBlank(renterTextCodeFlag)) {
            map.put("renterFlag", renterTextCodeFlag);
        }
        if (StringUtils.isNotBlank(ownerTextCodeFlag)) {
            map.put("ownerFlag", ownerTextCodeFlag);
        }
        if (!CollectionUtils.isEmpty(paramsMap)) {
            map.putAll(paramsMap);
        }
        return map;
    }
}
