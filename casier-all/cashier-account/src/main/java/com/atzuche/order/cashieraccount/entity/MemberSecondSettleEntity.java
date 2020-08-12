/**
 * 
 */
package com.atzuche.order.cashieraccount.entity;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class MemberSecondSettleEntity {
	private Integer memNo;
	private String orderNo;
	private Integer settleType;
	private Integer isSecondFlow;
}
