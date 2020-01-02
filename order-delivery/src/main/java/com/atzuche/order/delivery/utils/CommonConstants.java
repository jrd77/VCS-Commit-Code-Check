package com.atzuche.order.delivery.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @Author  zg 
 * @Date 2011-9-29
 * @Comments 
 */
public class CommonConstants {

	/** 日期/时间格式  "yyyyMMddHHmmss" */
	public static final SimpleDateFormat DATETIME_SEC_STR = new SimpleDateFormat("yyyyMMddHHmmss");

	/** 日期/时间格式  "yyyyMMddHHmmss"
	 * @throws ParseException */
    public static Date DATETIME_SEC_STR_PARSE(String date) throws ParseException {
    	synchronized(DATETIME_SEC_STR){
    		return DATETIME_SEC_STR.parse(date);
    	}
    }

	/**日期格式  "yyyy年MM月dd日 */
	private static final SimpleDateFormat DATE_YEAR_MONTH_DAY_CN = new SimpleDateFormat("yyyy年MM月dd日");
	public static String DATE_YEAR_MONTH_DAY_CN_FORMAT(Date date) {
		synchronized(DATE_YEAR_MONTH_DAY_CN){
			return DATE_YEAR_MONTH_DAY_CN.format(date);
		}
	}
}
 