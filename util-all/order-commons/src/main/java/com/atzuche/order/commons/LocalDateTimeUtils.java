package com.atzuche.order.commons;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import com.autoyol.commons.utils.StringUtils;
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
    public static final String YYYYMMDDHHMMSSS_PATTERN = "yyyyMMddHHmmss";

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
     * 日期格式yyyy年MM月dd日
     */
    public static String DATE_PATTERN_CHINESE = "yyyy年MM月dd日";


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
     * Date转LocalDateTime
     *
     * @param date
     *            Date对象
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        if(null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * LocalDateTime转换为Date
     *
     * @param dateTime
     *            LocalDateTime对象
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        if(dateTime==null){
            return null;
        }
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
    	if(dateTime == null) {
    		return "";
    	}
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

    public static LocalDate parseStringToLocalDate(String date,String format) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
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
     * 入参时间格式转换成LocalDateTime
     * @param dateTime
     * @return
     */
    public static LocalDateTime parseStringToDateTime(String dateTime) {
    	if(StringUtils.isBlank(dateTime)) {
    		return null;
    	}
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
        return localDateTime;
    }
    

    /**
     * 将localdate转化为  年月日时分秒格式的long格式
     * @param localDateTime
     * @return
     */
    public static long localDateTimeToLong(LocalDateTime localDateTime){
        return Long.valueOf(localdateToString(localDateTime,GlobalConstant.FORMAT_STR));
    }

    /**
     * 将LocalDateTime转为自定义的时间格式的字符串
     * @param localDateTime
     * @param format
     * @return
     */
    public static String localdateToString(LocalDateTime localDateTime, String format) {
        if(localDateTime == null){
            return null;
        }
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
    
    public static String localdateToStringChinese(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_CHINESE);
        return localDate.format(formatter);
    }
    /**
     * 简化版本 20200207
     * @param localDate
     * @return
     */
    public static String localdateToStringChinese(Date date) {
    	LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_CHINESE);
        return localDate.format(formatter);
    }
    
    /**
     * yyyy-MM-dd HH:mm:ss 
     */
    public static String dateToStringDefault(Date date) {
    	LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
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

    public static String getNowDateLong() {
        return formateLocalDateTimeStr(LocalDateTime.now(), GlobalConstant.DATE_TIME_FORMAT_2);
    }
    public static String formateLocalDateTimeStr(LocalDateTime localDateTime, DateTimeFormatter df) {
        return localDateTime.format(df);
    }

    /**
     * 获取 localDateTime n天之后的日期
     * @param localDateTime
     * @param n
     * @return
     */
    public static LocalDateTime getDateAfter(LocalDateTime localDateTime,int n) {
        localDateTime = LocalDateTime.now().plusDays(n);
        return localDateTime;
    }


    public static void main(String[] args) {
//        System.out.println(getDateAfter(LocalDateTime.now(),10));
//        String dateTime = formatEpochSecond("yyyy-MM-dd HH:mm:ss", 1525767228);
//        System.out.println(dateTime);
//        String parseStrToNewStr = parseStrToNewStr("yyyy-MM-dd HH:mm:ss", "2018-05-08 16:13:48", "MM-dd");
//        System.out.println(parseStrToNewStr);
//        long parseDateTime = parseDateTime("yyyy-MM-dd HH:mm:ss", "2018-05-08 16:13:48");
//        System.out.println(parseDateTime);
//        long parseDateTime2 = DefaultParseDateTime("2018-05-08 16:13:48");
//        System.out.println(parseDateTime2);
//        String yesterdayStr = yesterdayStr(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        System.out.println(yesterdayStr);
//        System.out.println(ZoneId.getAvailableZoneIds());
//
//        LocalDate localDate = parseStringToLocalDate("2019-12-14");
//        System.out.println(localDate);
//
//        System.out.println(localDateTimeToLong(LocalDateTime.now()));;
        LocalDateTime rentTime = LocalDateTime.of(2020,1,2,12,1,1);
        //long l = localDateTimeToLong(rentTime);

//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");//代替simpleDateFormat
//
//        String t= DefaultFormatEpochSecond(Long.valueOf("20200109212524"));
//        System.out.println(t);

//        String str1="20200109212524";
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime parse = LocalDateTime.parse(str1, dtf);
//        System.out.println(parse);
        //LocalDateTime da = parseStringToDateTime("20200110170824",YYYYMMDDHHMMSSS_PATTERN);
        //System.out.println(da);


    }

}
