package com.atzuche.order.sms.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
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

    /**
     * 单个对象的某个键的值
     * @return Object 键在对象中所对应得值 没有查到时返回空字符串
     */
    public static Object getValueByKey(Object obj, String key) {
        Class userCla = (Class) obj.getClass();
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            try {
                if (f.getName().endsWith(key)) {
                    System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 没有查到时返回空字符串
        return "";
    }
}
