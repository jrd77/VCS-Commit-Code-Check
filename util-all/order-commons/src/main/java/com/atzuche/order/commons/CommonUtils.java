package com.atzuche.order.commons;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @comments
 * @author zg 
 * @version 2013年9月25日 
 */
public class CommonUtils {

	private CommonUtils(){}

	private static final SimpleDateFormat DATE_SHORT_YEAR_MONTH = new SimpleDateFormat("yyMM");
	public static String DATE_SHORT_YEAR_MONTH_FORMAT(Date date) {
		synchronized(DATE_SHORT_YEAR_MONTH){
			return DATE_SHORT_YEAR_MONTH.format(date);
		}
	}
	/**
	 * 生成交易文件存的路径
	 * @param orderNo
	 * @return
	 */
	public static String createTransBasePath(String orderNo) {
		StringBuilder sb = getDatePath();
		sb.append(orderNo);
		sb.append("/");
		return sb.toString();
	}
	private static StringBuilder getDatePath(){
		Date date = new Date();
		String yearMonth = DATE_SHORT_YEAR_MONTH_FORMAT(date);
		StringBuilder sb = new StringBuilder(yearMonth);
		sb.insert(2, "/");
		sb.append("/");
		return sb;
	}
}
 
