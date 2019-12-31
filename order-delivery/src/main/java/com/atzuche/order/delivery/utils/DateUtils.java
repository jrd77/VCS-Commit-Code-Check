package com.atzuche.order.delivery.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 时间工具类
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

	public static final String DATE_DEFAUTE = "yyyyMMddHHmmss";
	public static final String DATE_TIME = "yyyyMMddHHmm";
	public static final String DATE_DEFAUTE_1 = "yyyy-MM-dd HH:mm";
    public static final String DATE_DEFAUTE_2 = "yyyy.MM.dd HH:mm";
	public static final String DATA_TIME_FORMAT_WITH_T = "yyyyMMdd'T'HHmmss";
	public static final String YEAR_FORMAT = "yyyy";
	public static final String TIME_DEFAUTE="HHmmss";
	public static final String YAER_DATE_DEFAUTE="yyyyMMdd";
	public static final String YAER_DATE_DEFAUTE1="yyyy.MM.dd";
	public static final String YAER_DATE_DEFAUTE2="yyyy-MM-dd";
	public static final String DATE_DEFAUTE_3 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final String DATE_DEFAUTE_4 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_DEFAUTE_5 = "yyyy年MM月dd日 HH:mm:ss";

	private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
	private final static String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
										"双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };


	/**
	 * 字符串转时间，字符串格式：yyyy-MM-dd HH:mm
	 * @param str
	 * @return
	 */
	public static Date parseDate(String str) {
		return parseDate(str, DATE_DEFAUTE_1);
	}

	public static LocalDateTime parseDate1(String str,String format){
		return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(format));
	}
	/**
	 * 字符串转时间
	 * @param str
	 * @param format
	 * @return
	 */
	public static Date parseDate(String str, String format) {
		try {
			return parseDate(str, new String[]{format});
		} catch (ParseException e) {
			logger.error("parse date error.", e);
		}
		return null;
	}
	
	public static Date formatDate(String date, String format) throws ParseException {
		
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.parse(date);
	}

    /**
	 * 字符串转时间
	 * @param str
	 * @param format
	 * @return
	 */
	public static LocalDateTime parseLocalDateTime(String str, String format){
		return LocalDateTime.parse(str,DateTimeFormatter.ofPattern(format));
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
	 * 时间转换成制定的格式
	 * @param date
	 * @param
	 * @return
	 */
	public static String formate(Date date, String format) {
		return DateFormatUtils.format(date, format);
	}

	/**
	 * 对yyyMMddHHmmss格式的时间加减天得到指定格式的字符串
	 * @param time
	 * @param day
	 * @param format
	 * @return
	 */
	public static String addDay(long time,int day,String format){
		try {
			Calendar  calendar = new  GregorianCalendar(); 
			Date date = CommonConstants.DATETIME_SEC_STR_PARSE(time+"");
			calendar.setTime(date); 
			calendar.add(Calendar.DAY_OF_MONTH, day);
			date = calendar.getTime();   //这个时间就是日期往后推一天的结果 
			return formate(date,format);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 对yyyMMddHHmmss格式的时间加减天得到指定格式的字符串
	 * @param time
	 * @param day
	 * @param format
	 * @return
	 */
	public static String addDay(Date time,int day,String format){
		Calendar  calendar = new  GregorianCalendar(); 
		calendar.setTime(time); 
		calendar.add(Calendar.DAY_OF_MONTH, day);
		time = calendar.getTime();   //这个时间就是日期往后推一天的结果 
		return formate(time,format);
	}
	public static Date addDay(Date time,int day){
		Calendar  calendar = new  GregorianCalendar(); 
		calendar.setTime(time); 
		calendar.add(Calendar.DAY_OF_MONTH, day);
		time = calendar.getTime();   //这个时间就是日期往后推一天的结果 
		return time;
	}


	public static Date addDay(long time,int day) throws ParseException{
		Date date = CommonConstants.DATETIME_SEC_STR_PARSE(time+"");
		Calendar  calendar = new  GregorianCalendar(); 
		calendar.setTime(date); 
		calendar.add(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
	
	/**
	 * 根据身份证号码获取几几后信息 如 80后 90后
	 *
	 * @param idNo
	 * @return
	 */
	public static String getBirthPeriodByIdCard(String idNo) {
		if (StringUtils.isEmpty(idNo) || idNo.length() != 18) {
			return null;
		}
		try {
			Integer birthYear = Integer.parseInt(idNo.substring(8, 10));
			return birthYear >= 90 ? "90后" : birthYear >= 80 ? "80后" : birthYear >= 70 ? "70后" : birthYear >= 60 ?
					"60后" : birthYear >= 50 ? "50后" : birthYear <= 9 ? "00后" : null;
		} catch (Exception e) {
			return null;
		}

	}


	/**
	 * 根据身份证号获取星座信息
	 *
	 * @param idNo
	 * @return
	 */
	public static String getConstellationByIdCard(String idNo) {
		try {
			if (StringUtils.isEmpty(idNo) || idNo.length() != 18) {
				return null;
			}
			String birthMonthStr = idNo.substring(10, 12);
			String birthDayStr = idNo.substring(12, 14);
			if (birthMonthStr.startsWith("0")) {
				birthMonthStr = birthMonthStr.substring(1, birthMonthStr.length());
			}
			if (birthDayStr.startsWith("0")) {
				birthDayStr = birthDayStr.substring(1, birthDayStr.length());
			}
			Integer birthMonth = Integer.parseInt(birthMonthStr);
			Integer birthDay = Integer.parseInt(birthDayStr);
			return getConstellation(birthMonth, birthDay);
		} catch (Exception e) {
			logger.error("常规异常处理,获取星座失败", e);
			return "";
		}
		
	}


	/**
	 * 通过生日计算星座
	 *
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getConstellation(int month, int day) {
		return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
	}

	public static Date addMin(Date time,int minute){
		Calendar  calendar = new  GregorianCalendar(); 
		calendar.setTime(time); 
		calendar.add(Calendar.MINUTE, minute);
		time = calendar.getTime();
		return time;
	}
	
	
	public static String addHour(long time,int hour) throws ParseException {
		Date date = CommonConstants.DATETIME_SEC_STR_PARSE(time+"");
		Calendar ca=Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.HOUR_OF_DAY, hour);
		return CommonConstants.DATE_YEAR_MONTH_DAY_CN_FORMAT(ca.getTime());
	}
	
	public static String addHourReturnTime(long time,int hour) throws ParseException {
		Date date = CommonConstants.DATETIME_SEC_STR_PARSE(time+"");
		Calendar ca=Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.HOUR_OF_DAY, hour);
		return formate(ca.getTime(),DATE_DEFAUTE);
	}

	public static Date getNowDate() {
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate ld = LocalDate.now();
		ZonedDateTime zdt = ld.atStartOfDay(zoneId);
		Date date = Date.from(zdt.toInstant());
		return date;
	}
	
	public static String formatTime(String date) throws ParseException {
		
		return parseDateDefaute(parseDateDefaute(date, DATE_DEFAUTE), DATE_DEFAUTE_3);
	}


	public static String parseDateDefaute(Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public static Date parseDateDefaute(String str, String format) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.parse(str);
	}
	
	/**
	 * 字符串转时间
	 * @param str
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateNoException(String str, String format) throws ParseException {
		return parseDate(str, new String[]{format});
	}

	public static Date formatNowDate() {
		SimpleDateFormat sdf=new SimpleDateFormat(YAER_DATE_DEFAUTE2);
		Date time=null;
		try {
			time= sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return time;
	}
	
	public static String format(String date, String format1, String format2) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat(format1);
		return formate(sdf.parse(date), format2);
	}
	
	public static Date formatNowDate(String dateFormat) {
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
		Date time=null;
		try {
			time= sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return time;
	}
	
	public static String getNowDateLong() {

		Date date = new Date();
		SimpleDateFormat sdf= new SimpleDateFormat(DATE_DEFAUTE);
		String str = sdf.format(date);
		return str;
	}

	public static List<Integer> getDate(Date day) {
		List<Integer> list = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
        cal.setTime(day);//设置起时间
        list.add(cal.get(Calendar.YEAR));
        list.add(cal.get(Calendar.MONTH) + 1);
        list.add(cal.get(Calendar.DATE));
        list.add(cal.get(Calendar.HOUR_OF_DAY));
        list.add(cal.get(Calendar.MINUTE));
        list.add(cal.get(Calendar.SECOND));
        return list;
	}

    public static Date addDate(Date day, int year, int month, int date){
        Calendar  calendar = new  GregorianCalendar();
        calendar.setTime(day);//设置起时间
        calendar.add(Calendar.YEAR, year);//增加一年
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, date);
        System.out.println("输出:"+calendar.getTime());
        day = calendar.getTime();
        //cal = null;
        return day;
    }

    /**
     * 字符串转时间
     * @param str
     * @param format
     * @return
     */
    public static LocalDate parseLocalDate(String str, String format){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(str,df);
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

	public static long today() {
		try {
			return Long.parseLong(DateFormatUtils.format(new Date(), YAER_DATE_DEFAUTE).trim());
		} catch (Exception e) {
			logger.error("today error. e", e);
		}
		return 0l;
	}

	private static final String[] possiblePatterns =
			{
					"yyyy-MM-dd",
					"yyyy-MM-dd HH",
					"yyyy-MM-dd HH:mm",
					"yyyy-MM-dd HH:mm:ss",
					"yyyy.MM.dd HH",
					"yyyy.MM.dd HH:mm",
					"yyyy.MM.dd HH:mm:ss",
					"yyyyMMdd",
					"yyyyMMdd HH",
					"yyyyMMdd HH:mm",
					"yyyyMMdd HH:mm:ss",
					"yyyy/MM/dd",
					"yyyy/MM/dd HH",
					"yyyy/MM/dd HH:mm",
					"yyyy/MM/dd HH:mm:ss",
					"yyyy MM dd",
					"yyyy MM dd HH",
					"yyyy MM dd HH:mm",
					"yyyy MM dd HH:mm:ss",
					"yyyy年MM月dd日",
					"yyyy年MM月dd日 HH",
					"yyyy年MM月dd日 HH:mm",
					"yyyy年MM月dd日 HH:mm:ss"
			};

	/**
	 * 各种格式的日期字符串转日期，如有增加，添加格式至上面的数组即可
	 *
	 * @param inputDate
	 * @return
	 */
	public static Date parseDateAll(String inputDate) {
		try {
			return org.apache.commons.lang.time.DateUtils.parseDate(inputDate, possiblePatterns);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("parse error. e", e);
		}
		return null;
	}

	/**
	 * 租期之间的相差时间
	 */
	public static Long durationDays(String rentTime,String revertTime){
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime orderStartTime = LocalDateTime.parse(rentTime,df);
		LocalDateTime orderEndtimeTime = LocalDateTime.parse(revertTime,df);
		Duration duration = Duration.between(orderStartTime,orderEndtimeTime);
		// 相差的天数
		Long days = duration.toDays();
		return days;
	}

	/**
	 * 礼品卡时间校验
	 * @param rentTime
	 * @param revertTime
	 * @param days
	 * @return
	 */
	public static Boolean durationDays(String rentTime,String revertTime,Integer days){
		if(days == null || days <= 0){
			return false;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime orderStartTime = LocalDateTime.parse(rentTime,df);
		LocalDateTime orderEndtimeTime = LocalDateTime.parse(revertTime,df);
		// 相差的小时数
		Long hours = ChronoUnit.HOURS.between(orderStartTime, orderEndtimeTime);
		if(hours <= days.intValue()*24){
			return true;
		}
		return false;
	}
}