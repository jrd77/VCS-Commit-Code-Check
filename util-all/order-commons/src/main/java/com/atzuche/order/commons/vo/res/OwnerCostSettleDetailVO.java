/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang 车主费用结算明细（结算后的）
 */
@Data
@ToString
public class OwnerCostSettleDetailVO {
	@AutoDocProperty(value = "订单号")
	private String orderNo;
	@AutoDocProperty(value = "车主会员号")
	private String memNo;
	
	@AutoDocProperty(value = "车主租金")
	private Integer rentAmt = 0;
	@AutoDocProperty(value = "违约罚金")
	private Integer fineAmt = 0;
	@AutoDocProperty(value = "租客车主互相调价")
	private Integer adjustAmt = 0;
	@AutoDocProperty(value = "给租客的优惠") // 租金补贴和车主券
	private Integer subsidyToRenterAmt = 0;

	@AutoDocProperty(value = "车主需支付给平台的费用")
	private Integer payToPlatformAmt = 0;
	
	@AutoDocProperty(value = "平台给车主的补贴")
	private Integer fromPlatformSubsidyAmt = 0;
	
	@AutoDocProperty(value = "平台服务费")
	private Integer platformSrvAmt = 0;

	@AutoDocProperty(value = "油费")
	private Integer oilAmt = 0;

	@AutoDocProperty(value = "超里程费用总额")
	private Integer mileageCostAmt = 0;

	@AutoDocProperty(value = "平台加油服务费")
	private Integer platformOilCostAmt = 0;

	@AutoDocProperty(value = "GPS押金")
	private Integer gpsDepositAmt = 0;

	@AutoDocProperty(value = "GPS服务费")
	private Integer gpsSrvAmt = 0;

	@AutoDocProperty(value = "配送服务费")
	private Integer getReturnCarAmt = 0;

	@AutoDocProperty(value = "本订单收益")
	private Integer incomeAmt = 0;

	@AutoDocProperty(value = "原欠费金额")
	private Integer oldDebtAmt = 0;

	@AutoDocProperty(value = "新欠费金额")
	private Integer newDebtAmt = 0;
}
