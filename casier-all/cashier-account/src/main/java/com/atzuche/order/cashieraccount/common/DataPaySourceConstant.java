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
public class DataPaySourceConstant {
	/**
	 * 01：手机银联
		02.:新银联（含银联和applepay统一商户号）
		06:支付宝支付，
		07:微信支付(App), 
		08:快捷支付（快钱）
		11.快捷支付（H5）     仅仅是source值不同。
		12:Apple Pay
		13. 微信支付(公众号)
		14.连连支付
		15. 微信支付(H5)
	 */
//	public static final String DEFAULT = "00";  //默认
	
	/**
	 * 银联
	 */
	public static final String UNIONPAY = "01";
	/**
	 * 线下支付
	 */
	public static final String AT_OFFLINE = "02";
	
	/**
	 * 支付宝
	 */
	public static final String ALIPAY = "06";
	/**
	 * 微信App
	 */
	public static final String WEIXIN_APP = "07";
	/**
	 * 快钱支付(统一app和H5)
	 */
	public static final String BILL99 = "08";
	/**
	 * Applepay
	 */
	public static final String APPLEPAY = "12";
	/**
	 * 微信公众号
	 */
	public static final String WEIXIN_MP = "13";
	/**
	 * 连连支付(统一app和H5)
	 */
	public static final String LIANLIANPAY = "14";
	/**
	 * 微信H5
	 */
	public static final String WEIXIN_H5 = "15";
	/**
	 * 默认
	 */
	public static final String DEFAULT = "99";
	
}
