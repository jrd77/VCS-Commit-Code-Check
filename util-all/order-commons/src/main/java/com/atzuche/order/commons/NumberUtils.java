/**
 * 
 */
package com.atzuche.order.commons;

/**
 * @author jing.huang
 * 显示的正负号转换，管理后台显示处理。
 */
public class NumberUtils {
	//正负号转换,转换为负数
	public static int convertNumberToFushu(Integer num) {
		if(num != null && num.intValue() > 0) {
			return -num;
		}
		return num;
//		return -Math.abs(num);
	}
	//转换为正数
	public static int convertNumberToZhengshu(Integer num) {
		if(num != null && num.intValue() < 0) {
			return -num;
		}
		return num;
//		return Math.abs(num); //绝对值
	}
	
	//正负号转换,转换为负数
	public static double convertNumberToFushu(Double num) {
		if(num != null && num.intValue() > 0) {
			return -num;
		}
		return num;
//		return -Math.abs(num);
	}
	//转换为正数
	public static double convertNumberToZhengshu(Double num) {
		if(num != null && num.intValue() < 0) {
			return -num;
		}
		return num;
//		return Math.abs(num); //绝对值  
	}
	
	//测试类
//	public static void main(String[] args) {
//		System.out.println(convertNumberToFushu(100));
//		System.out.println(convertNumberToFushu(100.4545));
//		System.out.println(convertNumberToFushu(-100));
//		System.out.println(convertNumberToFushu(-100.4545));
//		
//		System.out.println("---------------------------------------------");
//		
//		System.out.println(convertNumberToZhengshu(100));
//		System.out.println(convertNumberToZhengshu(100.4545));
//		System.out.println(convertNumberToZhengshu(-100));
//		System.out.println(convertNumberToZhengshu(-100.4545));
//	}
	
}
