package com.atzuche.order.commons;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
/*
 * @Author ZhangBin
 * @Date 2019/12/13 18:33
 * @Description: JAVA8 时间工具类
 *
 **/
public class LocalDateTimeUtils {

    /**
     * 默认的时间日期样式
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 中国时区
     */
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 日期格式yyyy-MM-dd
     */
    public static String DATE_PATTERN = "yyyy-MM-dd";


    /**
     * 构造函数
     */
    private LocalDateTimeUtils() {
        super();
    }

    /**
     * Date转LocalDateTime
     *
     * @param date
     *            Date对象
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param dateTime
     *            LocalDateTime对象
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime
     *            LocalDateTime对象
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_PATTERN);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime
     *            LocalDateTime对象
     * @param pattern
     *            要格式化的字符串
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = DEFAULT_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 获取今天的00:00:00
     *
     * @return
     */
    public static String getDayStart() {
        return getDayStart(LocalDateTime.now());
    }

    /**
     * 获取今天的23:59:59
     *
     * @return
     */
    public static String getDayEnd() {
        return getDayEnd(LocalDateTime.now());
    }

    /**
     * 获取某天的00:00:00
     *
     * @param dateTime
     * @return
     */
    public static String getDayStart(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(LocalTime.MIN));
    }

    /**
     * 获取某天的23:59:59
     *
     * @param dateTime
     * @return
     */
    public static String getDayEnd(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(LocalTime.MAX));
    }

    /**
     * 获取本月第一天的00:00:00
     *
     * @return
     */
    public static String getFirstDayOfMonth() {
        return getFirstDayOfMonth(LocalDateTime.now());
    }

    /**
     * 获取本月最后一天的23:59:59
     *
     * @return
     */
    public static String getLastDayOfMonth() {
        return getLastDayOfMonth(LocalDateTime.now());
    }

    /**
     * 获取某月第一天的00:00:00
     *
     * @param dateTime
     *            LocalDateTime对象
     * @return
     */
    public static String getFirstDayOfMonth(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN));
    }

    /**
     * 获取某月最后一天的23:59:59
     *
     * @param dateTime
     *            LocalDateTime对象
     * @return
     */
    public static String getLastDayOfMonth(LocalDateTime dateTime) {
        return formatDateTime(dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX));
    }
    /**
     * 获取某月最后一天的23:59:59
     *
     * @param time
     *            时间字符串
     * @param format
     *          格式
     * @return
     */
    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    /**
     * 字符换日期转化为Localdate
     * @param date
     *            日期字符串
     * @return
     */
    public static LocalDate parseStringToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * 按照默认的模板将时间戳转换为时间日期的字符串形式
     * 
     * @param epochSecond
     *            时间戳 1525767228
     * @return 返回时间日期的字符串形式 2018-05-08 16:13:48
     */
    public static String DefaultFormatEpochSecond(long epochSecond) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond),
                ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }


    /**
     * 按照给定的时间日期模版，将时间戳转换成字符串形式
     * 
     * @param pattern
     *            模版，例如"yyyy-MM-dd HH:mm:ss"
     * @param epochSecond
     *            时间戳 1525767228
     * @return 转换后的字符串 2018-05-08 16:13:48
     */


    public static String formatEpochSecond(String pattern, long epochSecond) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond),
                ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * 按照给定的时间日期模版，将时间戳转换成字符串形式
     * 
     * @param dateTimeFormatter
     *            模版
     * @param epochSecond
     *            时间戳
     * @return 转换后的字符串
     */
    public static String formatEpochSecond(DateTimeFormatter dateTimeFormatter, long epochSecond) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond),
                ZoneId.systemDefault());
        return localDateTime.format(dateTimeFormatter);
    }


    /**
     * 将一种时间日期字符串转换成另外一种形式
     * 
     * @param oldPattern
     *            旧的时间日期字符串样式 "yyyy-MM-dd HH:mm:ss"
     * @param oldDateTime
     *            旧的时间日期字符串 2018-05-08 16:13:48
     * @param newPattern
     *            新的时间日期字符串样式 "MM-dd"
     * @return 转换后的字符串 05-08
     */
    public static String parseStrToNewStr(String oldPattern, String oldDateTime, String newPattern) {
        LocalDateTime localDateTime = LocalDateTime.parse(oldDateTime, DateTimeFormatter.ofPattern(oldPattern));
        return localDateTime.format(DateTimeFormatter.ofPattern(newPattern));
    }


    /**
     * 将一种时间日期字符串转换成另外一种形式
     * 
     * @param oldDateTimeFormatter
     *            旧的时间日期字符串样式
     * @param oldDateTime
     *            旧的时间日期字符串
     * @param newDateTimeFormatter
     *            新的时间日期字符串样
     * @return 转换后的字符串
     */
    public static String parseStrToNewStr(DateTimeFormatter oldDateTimeFormatter, String oldDateTime,
                                          DateTimeFormatter newDateTimeFormatter) {
        LocalDateTime localDateTime = LocalDateTime.parse(oldDateTime, oldDateTimeFormatter);
        return localDateTime.format(newDateTimeFormatter);
    }


    /**
     * 将给定的时间日期字符串按照指定的模版解析成时间戳
     * 
     * @param pattern
     *            模版，例如"yyyy-MM-dd HH:mm:ss"
     * @param dateTime
     *            时间日期字符串 2018-05-08 16:13:48
     * @return 时间戳 1525767228000
     */
    public static long parseDateTime(String pattern, String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.toEpochMilli();
    }


    /**
     * 将给定的时间日期字符串按照指定的模版解析成时间戳
     * 
     * @param dateTimeFormatter
     *            模版
     * @param dateTime
     *            时间日期字符串
     * @return 时间戳
     */
    public static long parseDateTime(DateTimeFormatter dateTimeFormatter, String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.toEpochMilli();
    }


    /**
     * 将给定的时间日期字符串按照默认的模版解析成时间戳 "yyyy-MM-dd HH:mm:ss"
     * 
     * @param dateTime
     *            2018-05-08 16:13:48
     * @return 时间戳 1525767228000
     */
    public static long DefaultParseDateTime(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 将localdate转化为  年月日时分秒格式的long格式
     * @param localDateTime
     * @return
     */
    public static long localDateTimeToLong(LocalDateTime localDateTime){
        String longDateTime = String.valueOf(localDateTime.getYear()) +
                localDateTime.getMonthValue() +
                localDateTime.getDayOfMonth() +
                localDateTime.getHour() +
                localDateTime.getMinute() +
                localDateTime.getSecond() + "00";
        return Long.valueOf(longDateTime);
    }

    /**
     * 将LocalDateTime转为自定义的时间格式的字符串
     * @param localDateTime
     * @param format
     * @return
     */
    public static String localdateToString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }
    /**
     * 将LocalDate转为自定义的时间格式的字符串
     * @param localDateTime
     * @param format
     * @return
     */
    public static String localdateToString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return localDate.format(formatter);
    }

    /**
     * 按照给定的格式获取昨天这个时候的时间日期字符串
     * 
     * @param dateTimeFormatter
     *            时间日期格式
     * @return
     */
    public static String yesterdayStr(DateTimeFormatter dateTimeFormatter) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        return localDateTime.format(dateTimeFormatter);
    }


    /**
     * 按照给定的格式获取昨天这个时候的时间日期字符串
     * 
     * @param pattern
     *            时间日期格式
     * @return
     */
    public static String yesterdayStr(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * 按照给定的格式获取当前时间日期字符串
     * 
     * @param pattern
     *            时间日期格式
     * @return
     */
    public static String todayStr(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * 按照给定的格式获取当前时间日期字符串
     * 
     * @param dateTimeFormatter
     *            时间日期格式
     * @return
     */
    public static String todayStr(DateTimeFormatter dateTimeFormatter) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(dateTimeFormatter);
    }


    /**
     * 获取昨天这个时间的时间戳
     * 
     * @return 时间戳
     */
    public static long yesterday() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    public static void main(String[] args) {
        String dateTime = formatEpochSecond("yyyy-MM-dd HH:mm:ss", 1525767228);
        System.out.println(dateTime);
        String parseStrToNewStr = parseStrToNewStr("yyyy-MM-dd HH:mm:ss", "2018-05-08 16:13:48", "MM-dd");
        System.out.println(parseStrToNewStr);
        long parseDateTime = parseDateTime("yyyy-MM-dd HH:mm:ss", "2018-05-08 16:13:48");
        System.out.println(parseDateTime);
        long parseDateTime2 = DefaultParseDateTime("2018-05-08 16:13:48");
        System.out.println(parseDateTime2);
        String yesterdayStr = yesterdayStr(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(yesterdayStr);
        System.out.println(ZoneId.getAvailableZoneIds());

        LocalDate localDate = parseStringToLocalDate("2019-12-14");
        System.out.println(localDate);

        System.out.println(localDateTimeToLong(LocalDateTime.now()));;

    }

}
