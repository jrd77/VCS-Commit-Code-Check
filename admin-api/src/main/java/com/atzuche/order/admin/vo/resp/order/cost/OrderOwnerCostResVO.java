/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class OrderOwnerCostResVO {
	@AutoDocProperty(value="子订单编号")
	private String ownerOrderNo;

	@AutoDocProperty(value="预计收益")
	private String preIncomeAmt = "0";
	@AutoDocProperty(value="结算收益")
	private String settleIncomeAmt = "0";
	
	@AutoDocProperty(value="收益")
	private String incomeAmt = "0";
	
	@AutoDocProperty(value="车主租金")
	private String rentAmt = "0";

	@AutoDocProperty(value="违约罚金")
	private String fineAmt = "0";

	@AutoDocProperty(value="租客车主互相调价")
	private String adjustAmt = "0";
	
	@AutoDocProperty(value="超里程费用")  ///
	private String beyondMileAmt = "0";
	
	@AutoDocProperty(value="油费费用")  ///
	private String oilAmt = "0";
	
	@AutoDocProperty(value="加油服务费用")  ///
	private String addOilSrvAmt = "0";
	

	@AutoDocProperty(value="扣款")
	private String platformDeductionAmt = "0";

	@AutoDocProperty(value="服务费")
	private String platformSrvFeeAmt = "0";
	
	@AutoDocProperty(value="平台加油服务费")
	private String platformAddOilSrvAmt = "0";
	
	@AutoDocProperty(value="车主需支付给平台的费用")  ///
	private String ownerPayToPlatform = "0";
	
	@AutoDocProperty(value="gps服务费")
	private String gpsAmt = "0";

	@AutoDocProperty(value="gps押金")
	private String gpsDeposit = "0";
	
	@AutoDocProperty(value="配送服务费")
	private String carServiceSrvFee = "0";  //owner_order_increment_detail  车主增值服务表
	
	
	@AutoDocProperty(value="优惠抵扣")
	private String couponDeductionAmount = "0";
	
	@AutoDocProperty(value="车主券")  ///
	private String ownerCouponTitle = "0"; 
	
	@AutoDocProperty(value="车主券实际抵扣金额") ///
	private String ownerCouponAmt = "0";
	
	@AutoDocProperty(value="租客租金补贴金额") ///add 200116
	private String ownerSubsidyRentAmt = "0";
	
	
	@AutoDocProperty(value="平台补贴金额") ///
	private String platformSubsidyTotalAmt = "0";
	
	@AutoDocProperty(value="平台给车主的补贴") ///
	private String platformSubsidyAmt = "0";


    @AutoDocProperty(value="车主长租折扣")
    private String ownerLongRentDeduct;
    @AutoDocProperty(value="车主长租折扣实际抵扣金额")
    private String ownerLongRentDeductAmt;
	
}
