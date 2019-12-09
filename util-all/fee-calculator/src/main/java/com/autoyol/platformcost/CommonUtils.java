package com.autoyol.platformcost;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
	/**
	 * 获取向上取整天数
	 *
	 * 总小时/24 有余数加1天
	 * @param rentTime
	 * @param revertTime
	 * @return
	 * @throws ParseException
	 */
	public static long getDaysUpCeil(LocalDateTime rentTime, LocalDateTime revertTime) {
		Duration duration = Duration.between(rentTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();
		if ((minutes - hours*60) > 0) {
			hours = hours + 1;
		}
		if ((hours - days*24) > 0) {
			days = days + 1;
		}
		return days;
	}
	
	
	/**
	   * 计算租期
	 * @param rentTime
	 * @param revertTime
	 * @param configHours
	 * @return
	 */
	public static Double getRentDays(LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours) {
		Duration duration = Duration.between(rentTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();
		if ((minutes - hours*60) > 0) {
			hours = hours + 1;
		}
		System.out.println(days+","+hours);
		BigDecimal bdPremium = BigDecimal.ZERO;
		if ((hours - days*24) <= configHours) {
			bdPremium = new BigDecimal(1).divide(new BigDecimal(8)).multiply(new BigDecimal(hours - days*24));
		} else {
			days = days+1;
		}
		Double rentDays = new BigDecimal(days).add(bdPremium).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		return rentDays;
	}
	
	
	/**
	 * 计算提前时间
	 * @param rentTime
	 * @param getCarBeforeTime
	 * @return
	 */
	public static LocalDateTime calBeforeTime(LocalDateTime rentTime, Integer getCarBeforeTime){
		if (rentTime == null) {
			return null;
		}
		if (getCarBeforeTime == null) {
			return rentTime;
		}
		LocalDateTime beforeTime = rentTime.minusMinutes(getCarBeforeTime);
		return beforeTime;
	}
	
	/**
	 * 计算延后时间
	 * @param revertTime
	 * @param returnCarAfterTime
	 * @return
	 */
	public static LocalDateTime calAfterTime(LocalDateTime revertTime, Integer returnCarAfterTime){
		if (revertTime == null) {
			return null;
		}
		if (returnCarAfterTime == null) {
			return revertTime;
		}
		LocalDateTime afterTime = revertTime.plusMinutes(returnCarAfterTime);
		return afterTime;
	}
	
	
	/**
	 * 计算总小时数（计算日均价用）
	 * @param rentTime
	 * @param revertTime
	 * @return
	 */
	public static float getTotalHoursByRentAveragePrice(LocalDateTime rentTime, LocalDateTime revertTime) {
		Duration duration = Duration.between(rentTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long leftMinutes = minutes - hours*60;
		float H = 0.00f;
		if(leftMinutes > 0 && leftMinutes <= 15){
			H = hours + 0.25f;  
		}else if(leftMinutes > 15 && leftMinutes <= 30){
			H = hours + 0.5f;  
		}else if(leftMinutes > 30 && leftMinutes <= 45){
			H = hours + 0.75f;  
		}else if(leftMinutes > 45 && leftMinutes <= 60){
			H = hours + 1f;  
		}else{
			H = hours;  //没有分钟的情况
		}
		return H;
	}
	
	
	/**
	 * 计算起租时间到这一天的24点的小时数
	 * @param rentTime
	 * @param day
	 * @return
	 * @throws ParseException
	 */
	public static float getHolidayTopHours(LocalDateTime rentTime,String endDay) {
		// 59:59:59  ---> 23:59:59  24小时制
		LocalDateTime endTime = LocalDateTime.parse(endDay+" 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //今天的截止时间
		Duration duration = Duration.between(rentTime, endTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long leftMinutes = minutes - hours*60;
		float H = 0.00f;
		if(leftMinutes > 0 && leftMinutes <= 15){
			H = hours + 0.25f;  
		}else if(leftMinutes > 15 && leftMinutes <= 30){
			H = hours + 0.5f;  
		}else if(leftMinutes > 30 && leftMinutes <= 45){
			H = hours + 0.75f;  
		}else if(leftMinutes > 45 && leftMinutes <= 60){
			H = hours + 1f;  
		}else{
			H = hours;  //没有分钟的情况
		}
		return H;
	}
	
	
	/**
	 * 计算起租时间到这一天的24点的小时数
	 * @param rentTime
	 * @param day
	 * @return
	 * @throws ParseException
	 */
	public static float getHolidayFootHours(LocalDateTime revertTime,String beginDay) {
		// 59:59:59  ---> 23:59:59  24小时制
		LocalDateTime beginTime = LocalDateTime.parse(beginDay+" 00:00:01", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //今天的截止时间
		Duration duration = Duration.between(beginTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long leftMinutes = minutes - hours*60;
		float H = 0.00f;
		if(leftMinutes > 0 && leftMinutes <= 15){
			H = hours + 0.25f;  
		}else if(leftMinutes > 15 && leftMinutes <= 30){
			H = hours + 0.5f;  
		}else if(leftMinutes > 30 && leftMinutes <= 45){
			H = hours + 0.75f;  
		}else if(leftMinutes > 45 && leftMinutes <= 60){
			H = hours + 1f;  
		}else{
			H = hours;  //没有分钟的情况
		}
		return H;
	}
	
	
	/**
	 * 根据起租时间和还车时间得到日期列表  MAIN   huangjing 150826
	 * //列表只有1个，代表的是当天。
	   //列表只有2个，代表的是两天都是不完整的。
	   //列表大于3个，中间才有完整的一天
	 * @param rentTime
	 * @param revertTime
	 * @return
	 * @throws ParseException 
	 */
	public static List<String> getHolidayRentDays(LocalDateTime rentTime,LocalDateTime revertTime) {
		List<String> dateList = new ArrayList<String>();
		addHolidayRentDays(rentTime, revertTime, dateList);
		return dateList;
	}
	
	/**
	 * 获取天数
	 * @param calendar
	 * @param revertTime
	 * @param dateList
	 */
	private static void addHolidayRentDays(LocalDateTime rentTime, LocalDateTime revertTime, List<String> dateList) {
		dateList.add(rentTime.toLocalDate().toString());

		int rentMonth = rentTime.getMonthValue();
		int revertMonth = revertTime.getMonthValue();
		
		int lastDay = revertTime.getDayOfMonth();
		int currentDay = rentTime.getDayOfMonth();

		//同月同日时表示结束
		if(currentDay==lastDay&&rentMonth==revertMonth) {
			return;
		}
		else {
			rentTime=rentTime.plusDays(1);
			addHolidayRentDays(rentTime, revertTime, dateList);
		}
	}
	
	public static void main(String[] args) {
		LocalDateTime rentTime = LocalDateTime.of(2019, 12, 9, 15, 15, 0);
		LocalDateTime revertTime = LocalDateTime.of(2019, 12, 10, 19, 16, 0);
		System.out.println(getTotalHoursByRentAveragePrice(rentTime, revertTime));
	}
}
