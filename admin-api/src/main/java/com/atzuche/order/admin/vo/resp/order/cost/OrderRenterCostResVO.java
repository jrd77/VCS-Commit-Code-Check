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
public class OrderRenterCostResVO {
	@AutoDocProperty(value="子订单编号")
	private String renterOrderNo;

	@AutoDocProperty(value="租车费用-应收")
	private String rentFeeYingshou;

	@AutoDocProperty(value="租车费用-实收")
	private String rentFeeShishou;
	
	@AutoDocProperty(value="租车费用-应退")
	private String rentFeeYingtui = "0";

	@AutoDocProperty(value="租车费用-实退")
	private String rentFeeShitui = "0";
	
	
	@AutoDocProperty(value="租车费用-基础费用")  ///
	private String rentFeeBase;
	
	//--------------------------------------------
	@AutoDocProperty(value="租金")
	private String rentAmount;

	@AutoDocProperty(value="平台保障费  基础保障费")
	private String insuranceAmount;

	@AutoDocProperty(value="全面保障服务费 全面保障服务费")
	private String supperInsuranceAmount;

	@AutoDocProperty(value="手续费")
	private String serviceCharge;
	
	@AutoDocProperty(value="附加驾驶员险")
	private String additionalDriverInsuranceAmount;
	
	@AutoDocProperty(value="配送费(含超运能)")
	private String carServiceFee;
	
	@AutoDocProperty(value="取还车服务违约金(违约罚金)")
	private String carServiceFine;
	
	//--------------------------------------------
	
	@AutoDocProperty(value="租客需支付给平台的费用")  ///
	private String renterPayToPlatform;

	@AutoDocProperty(value="租客车主调价费用")  ///
	private String adjustAmt;
	
	@AutoDocProperty(value="超里程费用")  ///
	private String beyondMileAmt;
	
	@AutoDocProperty(value="油费费用")  ///
	private String oilAmt;
	
	@AutoDocProperty(value="加油服务费用")  ///
	private String addOilSrvAmt;

	//--------------------------------------------
	@AutoDocProperty(value="优惠抵扣")
	private String deductionAmount;
	//--------------------------------------------
	@AutoDocProperty(value="长租租金折扣文案")
	private String longDiscountDesc;
	@AutoDocProperty(value="长租租金折扣抵扣金额")
	private String longRentDeductAmt;
	@AutoDocProperty(value="车主券")  ///
	private String ownerCouponTitle; 
	
	@AutoDocProperty(value="车主券实际抵扣金额") ///
	private String ownerCouponAmt;
	
	@AutoDocProperty(value="平台券") ///
	private String platformCouponTitle; 
	
	@AutoDocProperty(value="平台券实际抵扣金额") ///
	private String platformCouponAmt;
	
	@AutoDocProperty(value="送取服务券") ///
	private String getReturnCouponTitle;
	
	@AutoDocProperty(value="送取服务券实际抵扣金额") ///
	private String getReturnCouponAmt;
	
	@AutoDocProperty(value="是否使用送取服务券") ///
	private String isUseGetReturnCoupon;
	@AutoDocProperty(value="是否使用钱包") ///
	private String isUseWallet;
	@AutoDocProperty(value="是否使用凹凸币") ///
	private String isUseAotuCoin;
	
	
	@AutoDocProperty(value="钱包余额") ///
	private String walletTotalAmt;
	
	@AutoDocProperty(value="钱包实际抵扣金额") ///
	private String walletAmt;
	
	@AutoDocProperty(value="凹凸币余额") ///
	private String aotuCoinTotalAmt;
	
	@AutoDocProperty(value="凹凸币实际抵扣金额") ///
	private String aotuCoinAmt;
	
	@AutoDocProperty(value="平台补贴金额") ///
	private String platformSubsidyTotalAmt;
	
	@AutoDocProperty(value="平台给租客的补贴") ///
	private String platformSubsidyAmt;
	
	@AutoDocProperty(value="平台实际给租客的补贴") ///
	private String platformSubsidyRealAmt;
	
	//--------------------------------------------
	@AutoDocProperty(value="车主给租客的补贴") ///add 200116
	private String ownerSubsidyTotalAmt;
	
	@AutoDocProperty(value="租客租金补贴") ///add 200116
	private String ownerSubsidyRentAmt;
	
	//--------------------------------------------
	@AutoDocProperty(value="车辆押金")
	private String vehicleDeposit;
	//--------------------------------------------
	
	@AutoDocProperty(value="车辆押金-应收")
	private String vehicleDepositYingshou;
	
	@AutoDocProperty(value="车辆押金-实收")
	private String vehicleDepositShishou;
	
	@AutoDocProperty(value="车辆押金-应退")
	private String vehicleDepositYingtui;
	
	@AutoDocProperty(value="车辆押金-实退")
	private String vehicleDepositShitui;
	
	@AutoDocProperty(value="平台任务减免金额")
	private String platformTaskFreeAmt;
	
	//--------------------------------------------
	@AutoDocProperty(value="违章押金")
	private String violationDeposit;
	//--------------------------------------------
	
	@AutoDocProperty(value="违章押金-应收")
	private String violationDepositYingshou;
	
	@AutoDocProperty(value="违章押金-实收")
	private String violationDepositShishou;
	
	@AutoDocProperty(value="违章押金-应退")
	private String violationDepositYingtui;
	
	@AutoDocProperty(value="违章押金-实退")
	private String violationDepositShitui;
	
	//--------------------------------------------
	@AutoDocProperty(value="补付费用-应收")
	private String paymentAmountYingshou;

	@AutoDocProperty(value="补付费用-实收")
	private String paymentAmountShishou;
	//--------------------------------------------

    @AutoDocProperty(value="车主长租折扣")
    private String ownerLongRentDeduct;
    @AutoDocProperty(value="车主长租折扣实际抵扣金额")
    private String ownerLongRentDeductAmt;

	//ADD
	@AutoDocProperty(value="租车费用-应扣")
	private String rentFeeYingkou = "0";
	@AutoDocProperty(value="租车费用-实扣")
	private String rentFeeShikou = "0";
	//Add
	@AutoDocProperty(value="车辆押金-应扣")
	private String vehicleDepositYingkou;
	@AutoDocProperty(value="车辆押金-实扣")
	private String vehicleDepositShikou;
	@AutoDocProperty(value="车辆押金-实收免押预授权")
	private String vehicleDepositShishouAuth;
	//Add
	@AutoDocProperty(value="违章押金-应扣")
	private String violationDepositYingkou;
	@AutoDocProperty(value="违章押金-实扣")
	private String violationDepositShikou;
	@AutoDocProperty(value="违章押金-实收免押预授权")
	private String violationDepositShishouAuth;

}
