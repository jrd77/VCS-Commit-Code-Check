package com.atzuche.order.sms.utils;

import com.atzuche.order.commons.DateUtils;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

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
    public static Map<String, Date> getLimitTimeBeforeHours(double hours) {
        Calendar calendar = new GregorianCalendar();
        System.out.println(DateUtils.formate(calendar.getTime(), DateUtils.DATE_DEFAUTE1));
        calendar.add(Calendar.MINUTE, (Double.valueOf(hours * 60).intValue() - 5));
        Date minTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, 15);
        Date maxTime = calendar.getTime();
        Map<String, Date> map = Maps.newHashMap();
        map.put("minTime", minTime);
        map.put("maxTime", maxTime);
        return map;
    }

    public static Boolean isArriveRentTime(Date rentTime,double hours)
    {
        boolean result = false;
        Map<String, Date> dateMap = getLimitTimeBeforeHours(hours);
        long minTime = 0L;
        long maxTime = 0L;
        if (dateMap.containsKey("minTime")) {
            minTime = (dateMap.get("minTime")).getTime();
        }
        if (dateMap.containsKey("maxTime")) {
            maxTime = (dateMap.get("maxTime")).getTime();
        }
        if (rentTime.getTime() > minTime && rentTime.getTime() < maxTime) {
            result = true;
            return result;
        }
        return result;
    }

    public static long getDateLatterCompareNowScoend(LocalDateTime time, int num){
        LocalDateTime timeLatter = time.plusMinutes(num);
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(timeLatter)){
            return 0;
        }
        Duration duration = Duration.between(now,timeLatter);
        return duration.toMinutes();
    }
}
