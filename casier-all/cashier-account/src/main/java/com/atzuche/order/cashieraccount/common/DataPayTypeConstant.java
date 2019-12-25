/**
 * 
 */
package com.atzuche.order.cashieraccount.common;

/**
 * @author jing.huang
 * @function
 * @date 2018年8月29日
 * @version
 */
public class DataPayTypeConstant {
	/**
	 * 支付方式：transType "01"：消费，"02"：预授权，
		消费方式："31"：消费撤销，"32"：预授权撤销，"03"：预授权完成，"04"：退货
	 */
	
	/**
	 * 支付 
	 */
	public static final String PAY_PUR = "01";
	public static final String PAY_PRE = "02";
	
	/**
	 * 退款
	 */
	public static final String PUR_VOID = "31";
	public static final String PRE_VOID = "32";
	public static final String PRE_FINISH = "03";
	public static final String PUR_RETURN = "04";
	
	
	
}
