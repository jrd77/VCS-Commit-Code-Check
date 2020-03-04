/**
 * 
 */
package com.atzuche.order.cashieraccount.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author jing.huang
 *
 */
public class SettleAmountException extends OrderException {

	public SettleAmountException() {
		super(ErrorCode.CASHIER_PAY_SETTLEAMT_FAIL_ERRER.getCode(), ErrorCode.CASHIER_PAY_SETTLEAMT_FAIL_ERRER.getText());
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	
}
