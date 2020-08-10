package com.atzuche.order.commons;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATE_DEFAUTE = "yyyyMMddHHmmss";
    public static final String DATE_DEFAUTE1 = "yyyy-MM-dd HH:mm:ss";
    public static final String fmt_yyyyMMdd = "yyyy-MM-dd";
    public static final String fmt_next_yyyyMMdd = "yyyy/MM/dd";
    public static final String FORMAT_STR_RENYUN = "yyyy-MM-dd HH:mm";
    public static long  START_TIME = 20191001000000L;
    public static long  END_TIME = 20191007235959L;

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

    /**
     * 字符串转时间
     * @param localDateTime
     * @param format
     * @return
     */
    public static String formate(LocalDateTime localDateTime, String format){
        return DateTimeFormatter.ofPattern(format).format(localDateTime);
    }


    /**
     * 字符串转时间
     * @param date
     * @param format
     * @return
     */
    public static String formate(Date date, String format){
    	//非空处理
    	if(date == null) {
    		return "";
    	}
    	try {
    		Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            return DateTimeFormatter.ofPattern(format).format(localDateTime);
		} catch (Exception e) {
			logger.error("formate exception date=[{}],format=[{}] e:",date,format,e);
		}
        return "";
    }

    /**
     * 字符串转时间
     * @param localDateTime 日期
     * @param format 指定格式
     * @return long
     */
    public static long formateLong(LocalDateTime localDateTime, String format){
        return Long.parseLong(formate(localDateTime, format));
    }

    /**
     * 字符串转时间
     * @param str
     * @param format
     * @return
     */
    public static Date parseDate(String str, String format) {
        try {
            return org.apache.commons.lang.time.DateUtils.parseDate(str, new String[]{format});
        } catch (ParseException e) {
            logger.error("parse date error.", e);
        }
        return null;
    }

    public static Date localDateTimeToDate(LocalDateTime time){
        if(time == null){
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = time.atZone(zone).toInstant();
        return Date.from(instant);
    }
    private static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static LocalDate minDays(Integer days) {
        LocalDate now = LocalDate.now();
        return now.minusDays(days);
    }

    public static boolean isFestival(long startTime, long endTime) {
        //国庆
        long springFestivalStartTime = START_TIME;
        long springFestivalEndTime = END_TIME;
        if (endTime <= startTime) {
            return false;
        }
        if (startTime > springFestivalEndTime || endTime < springFestivalStartTime) {
            return false;
        }
        return true;
    }

    public static Date firstDayOfYear(){
        LocalDate now = LocalDate.now();
        LocalDate firstYear = now.with(TemporalAdjusters.firstDayOfYear());
        return localDateToDate(firstYear);
    }

    public static long getDateLatterCompareNowScoend(LocalDateTime time,int num){
        LocalDateTime timeLatter = time.plusHours(num);
        LocalDateTime now = LocalDateTime.now();
        if(timeLatter.isAfter(now)){
            return 0;
        }
        Duration duration = Duration.between(timeLatter,now);
        return duration.getSeconds();
    }
    
    
    public static String getYearMonthFormate(String startTime, String endTime) {
    	String term = "";
    	try {
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt_yyyyMMdd);
    		LocalDate startDate = LocalDate.parse(startTime, formatter);
    		LocalDate endDate = LocalDate.parse(endTime, formatter);
    		int start_month = startDate.getMonthValue();	//开始日期月份
    		int start_day = startDate.getDayOfMonth();		//开始日期天数
    		int end_year = endDate.getYear();				//结束日期年份
    		int end_month = endDate.getMonthValue();		//结束日期月份
    		int end_day = endDate.getDayOfMonth();			//结束日期天数

    		long y = ChronoUnit.YEARS.between(startDate, endDate);		//计算两个日期间的年
    		long m = ChronoUnit.MONTHS.between(startDate, endDate);		//计算两个日期间的月
    		long d = ChronoUnit.DAYS.between(startDate, endDate);		//计算两个日期间的天

    		int lastDayOfEndDate = endDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();//获取日期月份的最后一天
    		if (start_day == end_day || lastDayOfEndDate == end_day) {
    			m = end_month - start_month;
    			d = 0;
    		} else if (end_day > start_day) {
    			d = endDate.getDayOfMonth() - startDate.getDayOfMonth();
    		} else {
    			String tmpY = "";
    			String tmpM = "";
    			String tmpD = "";
    			if(end_month == 1) {
    				tmpY = String.valueOf(end_year-1);
    				tmpM = String.valueOf(12);
    				tmpD = String.valueOf(start_day);
    			}else {
    				tmpY = String.valueOf(end_year);
    				if (end_month < 10) {
    					tmpM = "0" + (end_month - 1);
    				} else {
    					tmpM = String.valueOf(end_month - 1);
    				}
    				tmpD = String.valueOf(start_day);
    			}
    			String tmpTime = tmpY + tmpM + tmpD;
    			LocalDate tmpDate = LocalDate.parse(tmpTime, formatter);
    			d = ChronoUnit.DAYS.between(tmpDate, endDate);
    		}
    		if (m >= 12) {
    			m = m - y * 12;
    		}
    		term = (y == 0 ? "" : y + "年") + (m == 0 ? "" : +m + "个月");
    	} catch (Exception e) {
    		term = startTime + "-" + endTime;
    		logger.error("getYearMonthDayFormate exception:", e);
    	}
    	return term;
    }

}
