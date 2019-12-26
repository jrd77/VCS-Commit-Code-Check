/**
 * 
 */
package com.atzuche.order.cashieraccount.common;

/**
 * @author jing.huang   代替invoker
 * 网关不关心具体的支付业务，只对接业务的APPID
 *
 */
public class DataAppIdConstant {
	//================================== 支付分类 ==================================
	/**
	 * 短租
	 */
	public static final String APPID_SHORTRENT = "20";
	/**
	 * 长租
	 */
	public static final String APPID_LONGRENT = "21";
	/**
	 * PMS
	 */
	public static final String APPID_PMSRENT = "22";
	/**
	 * 套餐
	 */
	public static final String APPID_PACKAGERENT = "23";
	
}
