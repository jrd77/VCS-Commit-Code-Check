package com.atzuche.order.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


/**
 * @comments
 * @author zg 
 * @version 2013年9月25日 
 */
public class CommonUtils {

	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

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

	public static String getLocalAddress(){
		try{
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				if("eth1".equals(netInterface.getName())){
					Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						ip = (InetAddress) addresses.nextElement();
						if (ip != null && ip instanceof Inet4Address) {
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return "";
	}
}
 
