package com.atzuche.order.coreapi.utils;

import com.atzuche.order.commons.DateUtils;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author 胡春林
 *
 */
@Component
public class SMSTaskDateTimeUtils {

    /**
     * 用户查询的时间条件
     * minTime = (currTime-hours)-5Minutes
     * maxTime = minTime + 10Minutes
     * @param hours
     * @return {minTime, maxTime}
     */
    public static Map<String, Date> getLimitTimeBeforeHours(int hours) {
        Calendar calendar = new GregorianCalendar();
        System.out.println(DateUtils.formate(calendar.getTime(), DateUtils.DATE_DEFAUTE1));
        calendar.add(Calendar.MINUTE, (hours * 60 - 5));
        Date minTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, 15);
        Date maxTime = calendar.getTime();
        Map<String, Date> map = Maps.newHashMap();
        map.put("minTime", minTime);
        map.put("maxTime", maxTime);
        return map;
    }
}
