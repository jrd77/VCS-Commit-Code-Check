package com.atzuche.order.admin.util;

import com.atzuche.order.commons.ListUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @comments
 * @author zg 
 * @version 2013年9月25日 
 */
public class CommonUtils {
	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
	
	private static final char[] charAndNumArr = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	//private static final char[] charArr = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
//	private static final char[] UpCharAndNumArr = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};
	private static final char[] UpCharAndNumArr = {'a','b','c','d','e','f','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z','2','3','4','5','6','7','8'};
	private static final char[] numArr = {'0','1','2','3','4','5','6','7','8','9'};
	private static final char[] numArrNoZero = {'1','2','3','4','5','6','7','8','9'};
	private static final Random random = new Random();
	
	/**
	 * 礼品卡号
	 * @param dateAndBatchId 当天日期("yyMMdd" ) + 批次号（反转后的2位批次号）
	 * @return
	 */
	public static String generateGiftCardNo(String dateAndBatchId){
		StringBuilder sb = new StringBuilder(dateAndBatchId);
		
		for (int i = 0; i < 7; i++) {
			sb.append(numArr[random.nextInt(10)]);
		}
		sb.append(numArrNoZero[random.nextInt(9)]);
		sb.reverse();
		return sb.toString();
	}
	
	/**
	 * 9位的随机数字
	 * @return
	 */
	public static int getRandomId(){
		
		return Integer.valueOf(CommonUtils.getRandomNum(9));
	}
	
	/**
	 * 6位短信验证码
	 * @return
	 */
	public static String getSmsCode(){
		return getRandomNum(6);
	}
	/**
	 * 8个长度的随机字符串(包含：字母、数字)
	 * @return
	 */
	public static String getResetPwdVerifyCode(){
		
		return getRandom(charAndNumArr, 8);
	}
	/**
	 * 6个长度的随机密码
	 * @return
	 */
	public static String getNewRandomPwd(){
		
		return getRandom(numArr, 6);
	}

	
	public static String getLocalAddress(){
		try {
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
	
	public static String getRandomNum(int length) { 
	    return getRandom(numArr, length); 
	}
	
	public static String getRandomNumUpChar(int length) { 
	    return getRandom(UpCharAndNumArr, length); 
	}
	
	private static String getRandom(char[] charArray, int length){
		StringBuilder sb = new StringBuilder(); 
		int range = charArray.length; 
	    for (int i = 0; i < length; i++) {
			sb.append(charArray[random.nextInt(range)]);
		}
		return sb.toString();
	}
	
	//private static final SimpleDateFormat sdf_yyMMdd = new SimpleDateFormat(CommonConstants.DATE_SHORT_STR);

	
	/**
	 * 生成长度为“fullLenght”的序列号，不足的位补“0”
	 * @param fullLenght 总长度
	 * @param seq 序列
	 * @return
	 */
	public static String fixedLengthSeq(int fullLenght,int seq){
		fullLenght = fullLenght + 1;
		String seqStr = String.valueOf(seq);
		for(int i = 0; i < (fullLenght - seqStr.length()); i++ ){
			seqStr = "0" + seqStr;
		}
		return seqStr;
	}
	/*public static void main(String[] args) {
		System.out.println(fixedLengthSeq(5,1));
	}*/
	
	
	/*public static void main(String[] args) {

	}*/
	
	public static String imgToBase64(String fileName) throws FileNotFoundException{
		String base64Str = null;
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			base64Str = Base64.getEncoder().encodeToString(bytes);
		}catch(FileNotFoundException e){
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return base64Str;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map convertBean(Object bean) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object map2Bean(Class type, Map map) throws IntrospectionException, IllegalAccessException, InstantiationException,InvocationTargetException {
		Object obj = type.newInstance(); // 创建 JavaBean 对象
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类信息
		PropertyDescriptor[] proDesc = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < proDesc.length; i++) {
			PropertyDescriptor descriptor = proDesc[i];
			String propertyName = descriptor.getName();
			//descriptor.
			if (map.containsKey(propertyName)) {
				Object value = map.get(propertyName);
				descriptor.getWriteMethod().invoke(obj, value);
			}else{
				
			}
		}
		return obj;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object mapToBean(Class type, Map map) throws IntrospectionException, IllegalAccessException, InstantiationException,InvocationTargetException {
		Object obj = type.newInstance(); // 创建 JavaBean 对象
		// 给 JavaBean 对象的属性赋值
		Field fieldlist[] = type.getDeclaredFields(); 
		for (Field field : fieldlist) {
			String propertyName = field.getName();
			Annotation[] anns = field.getAnnotations();
			System.out.print("propertyName:"+propertyName);
			Class classType = field.getType();
			/*int ttt = 3;
			String t = (classType)ttt;*/
			if(classType.equals(String.class)){
				System.out.println("是字符串");
			}
			if(classType.equals(Integer.class)){
				System.out.println("是数字");
			}
			
			//得到这个属性的set方法
			if(map.containsKey(propertyName)){
				String value = String.valueOf(map.get(propertyName)) ;
				PropertyDescriptor pd = new PropertyDescriptor(propertyName, type); 
				Method setMethod = pd.getWriteMethod();  
				setMethod.invoke(obj, value);
			}
		}
		return obj;
	}

	public static final SimpleDateFormat sdf_MM = new SimpleDateFormat();
	

	

	


	

    /**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException 
	 */
	public static String md5Encode(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes("utf-8"));
		byte b[] = md.digest();
		int i;
		StringBuilder buf = new StringBuilder("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();
	}
	
	/**
	 * 将List变成split分割的字符串
	 * 
	 * @param keys
	 * @return
	 */
	public static String getStrByMemNos(List<String> keys) {
		if (ListUtil.isEmpty(keys)) {
			return "-999999";
		} else {
			return ListUtil.reduce(keys, ",");
		}
	}

	/**
	 * 170401 简化处理
	 * @param orderNo
	 * @return
	 */
	public static boolean isRenterOrderNo(long orderNo){
//		Date currDate = new Date();
//		StringBuilder sb = new StringBuilder(CommonConstants.DATE_YEAR_FORMAT(currDate));  //当前年
//		sb.reverse();
//		System.out.println(sb.toString());
//		String str = sb.toString();
		String str = "06";
		if(String.valueOf(orderNo).endsWith(str)){
			return true;
		}else{
			return false;
		}
	}




	/**
	 * 手机号脱敏
	 * @param num
	 * @return 脱敏后的手机号
	 */
	public static String mobileSecure(Object num){
		String mobile = String.valueOf(num).trim();
		if (mobile.length()!=11){
			return null;
		}
		return StringUtils.left(mobile, 3).concat("****"+ StringUtils.right(mobile,4));
	}

}

