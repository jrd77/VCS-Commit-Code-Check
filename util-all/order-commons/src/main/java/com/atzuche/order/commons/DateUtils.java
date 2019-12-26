package com.atzuche.order.commons;

import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String DATE_DEFAUTE = "yyyyMMddHHmmss";

    /**
     *	 判断是否是夜间
     *
     * @param dateStr 目标日期
     * @param begin 夜间开始时间
     * @param end 夜间截止时间
     * @return
     */
    public static boolean isNight(String dateStr, Integer begin, Integer end) {
        if (StringUtils.isBlank(dateStr)) {
            return false;
        }
        if (begin == null || end == null || begin.intValue() == end.intValue()) {
            return isNight(dateStr);
        }
        LocalDateTime localDateTime = DateUtils.parseLocalDateTime(dateStr, DateUtils.DATE_DEFAUTE);
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        long rentTotalSeconds = (long) (hour * 3600 + minute * 60 + second);
        long beginTotalSeconds = begin * 3600;
        long endTotalSeconds = end * 3600;
        if (beginTotalSeconds > endTotalSeconds) {
            // 夜间时间段为大于等于22点小于等于7点
            if (rentTotalSeconds >= beginTotalSeconds || rentTotalSeconds <= endTotalSeconds) {
                return true;
            }
        } else {
            // 夜间时间段为大于等于0点小于等于7点
            if (rentTotalSeconds >= beginTotalSeconds && rentTotalSeconds <= endTotalSeconds) {
                return true;
            }
        }
        return false;
    }

    /**
     * 	判断是否是夜间(夜间：22~07)
     *
     * @param dateStr 目标日期
     * @return
     */
    public static boolean isNight(String dateStr) {
        LocalDateTime localDateTime = DateUtils.parseLocalDateTime(dateStr, DateUtils.DATE_DEFAUTE);
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        // 夜间时间段为大于等于22点小于等于7点
        if (hour >= 22 || hour < 7 || (hour == 7 && minute == 0 && second == 0)) {
            return true;
        }
        return false;
    }

    /**
     * 字符串转时间
     * @param str
     * @param format
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String str, String format){
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(format));
    }
}
