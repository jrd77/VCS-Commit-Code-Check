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
public class DataPayKindConstant {
	/**
	 * 支付款项，01：租车押金，02：违章押金， 03补付租车押金
		支付款项，06：充值,07:支付欠款
		坦客支付款项，04:行程费用，05:押金费用
	 */
	
	/**
	 * 租车押金
	 */
	public static final String RENT = "01";
	/**
	 * 违章押金
	 */
	public static final String DEPOSIT = "02";
	/**
	 * 补付租车押金
	 */
	public static final String RENT_INCREMENT = "03";
	
	/**
	 * 坦客-租车费用
	 */
	public static final String TK_FEE = "04";
	/**
	 * 坦客-押金费用
	 */
	public static final String TK_RENT = "05";
	
	/**
	 * 充值
	 */
	public static final String RECHARGE = "06";
	/**
	 * 欠款
	 */
	public static final String DEBT = "07";
	/**
	 * 补付租车押金,管理后台。v5.11 
	 */
	public static final String RENT_INCREMENT_CONSOLE = "08";
	/**
	 * 长租线上费用支付
	 */
	public static final String LONG_RENT="09";
	/**
	 * PMS
	 */
	public static final String PMS="10";
	/**
	 * 默认
	 */
	public static final String DEFAULT = "99";
	
}
