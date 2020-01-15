/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderOwnerCostResVO {
	@AutoDocProperty(value="子订单编号")
	private String ownerOrderNo;

	@AutoDocProperty(value="预计收益")
	private String preIncomeAmt;
	@AutoDocProperty(value="结算收益")
	private String settleIncomeAmt;
	
	@AutoDocProperty(value="收益")
	private String incomeAmt;
	
	@AutoDocProperty(value="车主租金")
	private String rentAmt;

	@AutoDocProperty(value="违约罚金")
	private String fineAmt;

	@AutoDocProperty(value="租客车主互相调价")
	private String adjustAmt;
	
	@AutoDocProperty(value="超里程费用")  ///
	private String beyondMileAmt;
	
	@AutoDocProperty(value="油费费用")  ///
	private String oilAmt;
	
	@AutoDocProperty(value="加油服务费用")  ///
	private String addOilSrvAmt;
	

	@AutoDocProperty(value="扣款")
	private String platformDeductionAmt;

	@AutoDocProperty(value="服务费")
	private String platformSrvFeeAmt;
	
	@AutoDocProperty(value="平台加油服务费")
	private String platformAddOilSrvAmt;
	
	@AutoDocProperty(value="车主需支付给平台的费用")  ///
	private String ownerPayToPlatform;
	
	@AutoDocProperty(value="gps服务费")
	private String gpsAmt;

	@AutoDocProperty(value="gps押金")
	private String gpsDeposit;
	
	@AutoDocProperty(value="配送服务费")
	private String carServiceSrvFee;  //owner_order_increment_detail  车主增值服务表
	
	
	@AutoDocProperty(value="优惠抵扣")
	private String couponDeductionAmount;
	
	@AutoDocProperty(value="车主券")  ///
	private String ownerCouponTitle; 
	
	@AutoDocProperty(value="车主券实际抵扣金额") ///
	private String ownerCouponAmt;
	
	@AutoDocProperty(value="平台补贴金额") ///
	private String platformSubsidyTotalAmt;
	
	@AutoDocProperty(value="平台给车主的补贴") ///
	private String platformSubsidyAmt;
	
}
