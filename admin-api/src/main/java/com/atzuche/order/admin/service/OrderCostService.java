/**
 * 
 */
package com.atzuche.order.admin.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.vo.req.cost.OwnerCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderOwnerCostResVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderRenterCostResVO;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostSettleResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;
import com.atzuche.order.open.service.FeignOrderCostService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.commons.web.ResponseData;

/**
 * @author jing.huang
 *
 */
@Service
public class OrderCostService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	FeignOrderCostService feignOrderCostService;
	@Autowired
	OrderService orderService;
	
	/**
	 * 
	 * @param renterCostReqVO
	 * @return
	 */
	public OrderRenterCostResVO calculateRenterOrderCost(RenterCostReqVO renterCostReqVO) throws Exception{
		OrderRenterCostResVO realVo = new OrderRenterCostResVO();
		
		//主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
        	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }
        
        OrderCostReqVO req = new OrderCostReqVO();
		req.setOrderNo(renterCostReqVO.getOrderNo());
		req.setMemNo(orderEntity.getMemNoRenter());
		req.setSubOrderNo(renterCostReqVO.getRenterOrderNo());
		
        ///子订单号
		realVo.setRenterOrderNo(renterCostReqVO.getRenterOrderNo());
		//默认值处理  调价目前没有
		realVo.setAdjustAmt("0");
		//加油服务费
		realVo.setAddOilSrvAmt("0");
		///租客需支付给平台的费用
		realVo.setRenterPayToPlatform("0");
		
		
		
		ResponseData<com.atzuche.order.commons.vo.res.OrderRenterCostResVO> resData = feignOrderCostService.orderCostRenterGet(req);
		if(resData != null) {
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data = resData.getData();
			if(data != null) {
				
				//租金费用  费用明细表renter_order_cost_detail   
				putRenterOrderCostDetail(realVo,data);
				
				//优惠抵扣  优惠抵扣 券，凹凸币，钱包   钱包抵扣
				putRenterOrderDeduct(realVo,data);
				
				//租车押金和违章押金   车辆押金  违章押金
				putRenterOrderDeposit(realVo,data);
				
				//取还车服务违约金   renter_order_fine_deatail  取还车服务违约金
				putRenterOrderFine(realVo,data);
				
				//租车费用应收@海豹   实收
				putPaymentAmount(realVo,data);
				
				//油费,超里程
				putOilBeyondMile(realVo,data);
				
				//平台给租客的补贴
				putConsoleSubsidy(realVo,data);
				
				//补付费用 
				putSupplementAmt(realVo,data);
				
				//rentFeeBase基础费用
				putRentFeeBase(realVo,data);
			}
		}
		
		return realVo;
	}
	
	private void putRentFeeBase(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		//租客租金
		int rentAmt = Integer.valueOf(realVo.getRentAmount());
		//基础保障费
		int insure = Integer.valueOf(realVo.getInsuranceAmount());
//		附加驾驶员险
		int drive = Integer.valueOf(realVo.getAdditionalDriverInsuranceAmount());
		//违约罚金
		int fine = Integer.valueOf(realVo.getCarServiceFine());
		//调价
		int adjust = Integer.valueOf(realVo.getAdjustAmt());
		//油费
		int oil = Integer.valueOf(realVo.getOilAmt());
		//手续费
		int fee = Integer.valueOf(realVo.getServiceCharge());
		//全面保障
		int insureAll = Integer.valueOf(realVo.getSupperInsuranceAmount());
		//配送
		int getReturnFee = Integer.valueOf(realVo.getCarServiceFee());
		//租客需支付给平台的费用
		int rentPayToPlatform = Integer.valueOf(realVo.getRenterPayToPlatform());
		//超里程
		int beyondMile = StringUtils.isNotBlank(realVo.getBeyondMileAmt())?Integer.valueOf(realVo.getBeyondMileAmt()):0;
		//加油服务费
		int addOil = Integer.valueOf(realVo.getAddOilSrvAmt());
		
		int base = rentAmt + insure + drive + fine + adjust + oil + fee + insureAll + getReturnFee + rentPayToPlatform + beyondMile + addOil;
		
		realVo.setRentFeeBase(String.valueOf(base));
	}

	private void putSupplementAmt(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		String paymentAmountShishou = "";
		String paymentAmountYingshou = "";
		int yingshouAmt = 0;
		int shishouAmt = 0;
		List<OrderSupplementDetailEntity> supplementList = data.getSupplementList();
		for (OrderSupplementDetailEntity orderSupplementDetailEntity : supplementList) {
			yingshouAmt += orderSupplementDetailEntity.getAmt().intValue();
			if(orderSupplementDetailEntity.getPayFlag() == 0 || orderSupplementDetailEntity.getPayFlag() == 3 || orderSupplementDetailEntity.getPayFlag() == 10 || orderSupplementDetailEntity.getPayFlag() == 20) {
				shishouAmt += orderSupplementDetailEntity.getAmt().intValue();
			}
		}
		paymentAmountShishou = String.valueOf(shishouAmt);
		paymentAmountYingshou = String.valueOf(yingshouAmt);
		realVo.setPaymentAmountShishou(paymentAmountShishou);
		realVo.setPaymentAmountYingshou(paymentAmountYingshou);
		
	}

	private void putConsoleSubsidy(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		String platformSubsidyTotalAmt = "";
		String platformSubsidyAmt = "";
		String platformSubsidyRealAmt = "";
		
		//add 200116  车主给租客的补贴
		String ownerSubsidyTotalAmt;
		///add 200116  租客租金补贴
		String ownerSubsidyRentAmt;
		
		int platformSubsidyAmount = 0;
		///
		int ownerSubsidyRentTotalAmount = 0;
		int ownerSubsidyRentAmount = 0;
		
		List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = data.getOrderConsoleSubsidyDetails();
		for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : orderConsoleSubsidyDetails) {
			//`subsidy_source_name` varchar(16) DEFAULT NULL COMMENT '补贴来源方 1、租客 2、车主 3、平台',
			//  `subsidy_target_name` varchar(16) DEFAULT NULL COMMENT '补贴方名称 1、租客 2、车主 3、平台',
			if("3".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceName()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetName())) {
				platformSubsidyAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
			}
			
			if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceName()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetName())) {
				//车主给租客的补贴
				ownerSubsidyRentTotalAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				//租金的补贴
				if(orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo())) {
					ownerSubsidyRentAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
			}
		}
		
		platformSubsidyTotalAmt = String.valueOf(platformSubsidyAmount);
		platformSubsidyAmt = String.valueOf(platformSubsidyAmount);
		platformSubsidyRealAmt = String.valueOf(platformSubsidyAmount);
		///车主给租客的租金补贴
		ownerSubsidyTotalAmt = String.valueOf(ownerSubsidyRentTotalAmount);
		ownerSubsidyRentAmt = String.valueOf(ownerSubsidyRentAmount);
		
		
		realVo.setPlatformSubsidyTotalAmt(platformSubsidyTotalAmt);
		realVo.setPlatformSubsidyRealAmt(platformSubsidyRealAmt);
		realVo.setPlatformSubsidyAmt(platformSubsidyAmt);
		///
		realVo.setOwnerSubsidyTotalAmt(ownerSubsidyTotalAmt);
		realVo.setOwnerSubsidyRentAmt(ownerSubsidyRentAmt);
	}

	private void putOilBeyondMile(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		RenterOrderCostDetailEntity oilAmt = data.getOilAmt();
		if(oilAmt != null) {
			realVo.setOilAmt(String.valueOf(oilAmt.getTotalAmount()));
		}else {
			realVo.setOilAmt("0");
		}
		RenterOrderCostDetailEntity mileageAmt = data.getMileageAmt();
		if(mileageAmt != null) {
			realVo.setOilAmt(String.valueOf(mileageAmt.getTotalAmount()));
		}else {
			realVo.setBeyondMileAmt("0");
		}
	}

	private void putRenterOrderFine(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		int carServiceFine = 0;
		List<RenterOrderFineDeatailResVO> fineLst = data.getFineLst();
		for (RenterOrderFineDeatailResVO renterOrderFineDeatailResVO : fineLst) {
			//fine_type  罚金类型：1-修改订单取车违约金，2-修改订单还车违约金
			if(renterOrderFineDeatailResVO.getFineType().intValue() == 1) {
				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
			}
			if(renterOrderFineDeatailResVO.getFineType().intValue() == 2) {
				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
			}
		}
		//违约罚金
		realVo.setCarServiceFine(String.valueOf(carServiceFine));
	}
	
	private void putRenterOrderDeposit(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		realVo.setVehicleDeposit(data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---");
		realVo.setViolationDeposit(data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---");
		//
		realVo.setVehicleDepositYingshou(data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---");
		realVo.setVehicleDepositShishou(data.getRentVo()!=null?String.valueOf(data.getRentVo().getShifuDepositAmt()):"---");
		//海豹后面表里要加上的。 海豹的表里面有的
		realVo.setVehicleDepositYingtui("---"); //data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---"
		realVo.setVehicleDepositShitui("---"); //data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---"
		//任务减免金额
		realVo.setPlatformTaskFreeAmt(data.getRentVo()!=null?String.valueOf(data.getRentVo().getReductionAmt()):"---");
		
		//
		realVo.setViolationDepositYingshou(data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---");
		realVo.setViolationDepositShishou(data.getWzVo()!=null?String.valueOf(data.getWzVo().getShishouDeposit()):"---");
		//海豹后面表里要加上的。 海豹的表里面有的
		realVo.setViolationDepositYingtui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
		realVo.setViolationDepositShitui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
	}

	private void putRenterOrderDeduct(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		int walletAmt = data.getWalletCostDetail().getAmt();
		List<RenterOrderSubsidyDetailResVO> subsidyLst = data.getSubsidyLst();
		//优惠券抵扣金额
		String plateformCouponCashNo =  RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo();
		//车主券抵扣金额
		String ownerCouponCashNo =  RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo();
		//凹凸币抵扣金额
		String aotuCoinCashNo =  RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo();
		//送取服务券
		String getReturnCouponCashNo = RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo();
		
		//默认0
		int platformCoupon = 0;
		int ownerCoupon = 0;
		int aotuCoin = 0;
		int getReturnCoupon = 0;
				
		for (RenterOrderSubsidyDetailResVO renterOrderSubsidyDetailResVO : subsidyLst) {
			//12120010  12120051  12120012
			if(plateformCouponCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				platformCoupon +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}else if(ownerCouponCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				ownerCoupon +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}else if(aotuCoinCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				aotuCoin +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}else if(getReturnCouponCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				getReturnCoupon +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}
		}
		int deductionAmount = platformCoupon + ownerCoupon + aotuCoin + walletAmt + getReturnCoupon;
		realVo.setDeductionAmount(String.valueOf(deductionAmount));
		//车主券
		realVo.setOwnerCouponAmt(String.valueOf(ownerCoupon));
		//平台券
		realVo.setPlatformCouponAmt(String.valueOf(platformCoupon));
		//送取服务券
		realVo.setGetReturnCouponAmt(String.valueOf(getReturnCoupon));
		//钱包余额
		realVo.setWalletAmt(String.valueOf(walletAmt));
		//凹凸币
		realVo.setAotuCoinAmt(String.valueOf(aotuCoin));
		//钱包余额	暂不处理  下单的接口
		realVo.setWalletTotalAmt("---");
		//凹凸币余额   暂不处理 下单的接口
		realVo.setAotuCoinTotalAmt("---");
		/**
		 * 获取标题
		 */
		String platformCouponTitle = "";
		String ownerCouponTitle = "";
		String getReturnCouponTitle = ""; 
		List<OrderCouponEntity> orderCouponList = data.getOrderCouponList();
		for (OrderCouponEntity orderCouponEntity : orderCouponList) {
			if(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode() == orderCouponEntity.getCouponType().intValue()) {
				getReturnCouponTitle = orderCouponEntity.getCouponName();
			}else if(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode() == orderCouponEntity.getCouponType().intValue()) {
				ownerCouponTitle = orderCouponEntity.getCouponName();
			}else if(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode() == orderCouponEntity.getCouponType().intValue()) {
				platformCouponTitle = orderCouponEntity.getCouponName();
			}
		}
		//数据标题
		realVo.setPlatformCouponTitle(platformCouponTitle);
		realVo.setOwnerCouponTitle(ownerCouponTitle);
		realVo.setGetReturnCouponTitle(getReturnCouponTitle);
	}

	private void putRenterOrderCostDetail(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		/**
		 * 
		 */
		//租客租金
		String rentAmtCashNo =  RenterCashCodeEnum.RENT_AMT.getCashNo();
		//平台保障费
		String insureCashNo = RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo();
		//全面保障服务费
		String abatementCashNo = RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo();
		//附加驾驶员险
		String driverCashNo = RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo();
		//手续费
		String feeCashNo = RenterCashCodeEnum.FEE.getCashNo();
		//配送费用
		String getCostCashNo = RenterCashCodeEnum.SRV_GET_COST.getCashNo();
		String returnCostCashNo = RenterCashCodeEnum.SRV_RETURN_COST.getCashNo();
		String getBeyondCostCashNo = RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo();
		String returnBeyondCostCashNo = RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo();
		
		//默认0
		int rentAmount = 0;
		int insuranceAmount = 0;
		int supperInsuranceAmount = 0;
		int additionalDriverInsuranceAmount = 0;
		int serviceCharge = 0;
		int carServiceFee = 0;
		
		//费用列表
		List<RenterOrderCostDetailResVO> costList = data.getRenterOrderCostDetailList();
		if(costList != null) {
			
			for (RenterOrderCostDetailResVO renterOrderCostDetailResVO : costList) {
				if(rentAmtCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					rentAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
				}else if(insureCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					insuranceAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
				
				}else if(abatementCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					supperInsuranceAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}else if(driverCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					additionalDriverInsuranceAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}else if(feeCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					serviceCharge +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}else if(getCostCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					carServiceFee +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}else if(returnCostCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					carServiceFee +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}else if(getBeyondCostCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					carServiceFee +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}else if(returnBeyondCostCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					carServiceFee +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
					
				}
			}
		}
		//租客租金
		realVo.setRentAmount(String.valueOf(rentAmount));
		//基础保障费
		realVo.setInsuranceAmount(String.valueOf(insuranceAmount));
		//全面保障服务费
		realVo.setSupperInsuranceAmount(String.valueOf(supperInsuranceAmount));
		//附加驾驶员险
		realVo.setAdditionalDriverInsuranceAmount(String.valueOf(additionalDriverInsuranceAmount));
		//手续费
		realVo.setServiceCharge(String.valueOf(serviceCharge));
		//配送费用
		realVo.setCarServiceFee(String.valueOf(carServiceFee));
		
	}

	/**
	 * 租客 租车费用
	 * @param realVo
	 * @param data
	 */
	private void putPaymentAmount(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		//应收。
		realVo.setRentFeeYingshou(String.valueOf(data.getNeedIncrementAmt()));
		//实收
		AccountRenterCostSettleResVO renterSettleVo = data.getRenterSettleVo();
		realVo.setRentFeeShishou(String.valueOf(renterSettleVo.getShifuAmt()));
		//海豹后面表里要加上的。 海豹的表里面有的
		realVo.setRentFeeYingtui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
		realVo.setRentFeeShitui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
		
	}
	
	
	/**
	 * 
	 * @param ownerCostReqVO
	 * @return
	 */
	public OrderOwnerCostResVO calculateOwnerOrderCost(OwnerCostReqVO ownerCostReqVO) {
		OrderOwnerCostResVO realVo = new OrderOwnerCostResVO();
		
		OrderCostReqVO req = new OrderCostReqVO();
		req.setOrderNo(ownerCostReqVO.getOrderNo());
		req.setSubOrderNo(ownerCostReqVO.getOwnerOrderNo());
		ResponseData<com.atzuche.order.commons.vo.res.OrderOwnerCostResVO> resData = feignOrderCostService.orderCostOwnerGet(req);
		
		//子订单号
		realVo.setOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
		//默认值处理  调价目前没有
		realVo.setAdjustAmt("0");
		//加油服务费
		realVo.setAddOilSrvAmt("0");
		///车主需支付给平台的费用
		realVo.setOwnerPayToPlatform("0");
		
		
		if(resData != null) {
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data = resData.getData();
			if(data != null) {
				//给租客的优惠（车主券）和平台补贴
				putPlatformSubsidyAndOwnerCoupon(realVo,data);
				
				//扣款
				putPlatformDeductAmt(realVo,data);
				
				//收益
				putBaseFee(realVo,data);
				
				//最后算
				putIncome(realVo,data);
				
			}
		}else {
			logger.error("feign接口返回resData null!!! params={}",ownerCostReqVO.toString());
		}
		
		return realVo;
	}

	private void putIncome(OrderOwnerCostResVO realVo, com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		//预计收益
		 String preIncomeAmt = "0";
		 //结算收益
		 String settleIncomeAmt = "0";  //最终收益 income_amt （海豹要加上）
		 
		 int preIncomeAmtInt = 0;
		 int incomeAmt = Integer.valueOf(realVo.getIncomeAmt());
		 int platformDeductionAmt = Integer.valueOf(realVo.getPlatformDeductionAmt());
		 int couponDeductionAmount = Integer.valueOf(realVo.getCouponDeductionAmount());
		 int platformSubsidyTotalAmt = Integer.valueOf(realVo.getPlatformSubsidyTotalAmt());
		 preIncomeAmtInt = incomeAmt + platformDeductionAmt + couponDeductionAmount + platformSubsidyTotalAmt;
		 
		 preIncomeAmt = String.valueOf(preIncomeAmtInt);
		 realVo.setPreIncomeAmt(preIncomeAmt);
		 realVo.setSettleIncomeAmt(settleIncomeAmt);
		 
	}

	private void putBaseFee(OrderOwnerCostResVO realVo, com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		//收益
		String incomeAmt;
		
		//车主租金
		 String rentAmt;

		//违约罚金
		 String fineAmt;

		//租客车主互相调价
		 String adjustAmt = "0"; //默认
		
		//超里程费用
		 String beyondMileAmt = "0";  //
		
		//油费费用
		 String oilAmt = "0";  //
		
		//加油服务费用
		 String addOilSrvAmt = "0";  //海豹未结算?
		 
		 int incomeAmtInt = 0;
		 int adjustAmtInt = 0;
		 int addOilSrvAmtInt = 0;
		 int fineAmtInt = 0;
		 int rentAmtInt = 0;
		 int beyondMileAmtInt = 0;
		 int oilAmtInt = 0;
		 
				 
		 List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = data.getOwnerOrderFineDeatails();
		 if(ownerOrderFineDeatails != null) {
			 for (OwnerOrderFineDeatailEntity ownerOrderFineDeatailEntity : ownerOrderFineDeatails) {
				 //罚金来源编码（车主/租客/平台）1-租客，2-车主，3-平台
				if(ownerOrderFineDeatailEntity.getFineSubsidySourceCode().equals("2")) {
					fineAmtInt += ownerOrderFineDeatailEntity.getFineAmount().intValue();
				}
			}
		 }
		 fineAmt = String.valueOf(fineAmtInt);
		 
		 /**
		     * 获取车主费用列表
		     */
		 List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = data.getOwnerOrderPurchaseDetail();
		 if(ownerOrderPurchaseDetail != null) {
			 for (OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity : ownerOrderPurchaseDetail) {
				 if(ownerOrderPurchaseDetailEntity.getCostCode().equals(OwnerCashCodeEnum.RENT_AMT.getCashNo())) {
					 rentAmtInt += ownerOrderPurchaseDetailEntity.getTotalAmount().intValue();
	    		   }else if(ownerOrderPurchaseDetailEntity.getCostCode().equals(OwnerCashCodeEnum.OIL_COST_OWNER.getCashNo())) {
	    			   oilAmtInt += ownerOrderPurchaseDetailEntity.getTotalAmount().intValue();
	    		   }else if(ownerOrderPurchaseDetailEntity.getCostCode().equals(OwnerCashCodeEnum.MILEAGE_COST_OWNER.getCashNo())) {
	    			   beyondMileAmtInt += ownerOrderPurchaseDetailEntity.getTotalAmount().intValue();
	    		   }
			}
		 }
		 rentAmt = String.valueOf(rentAmtInt);
		 beyondMileAmt = String.valueOf(beyondMileAmtInt);
		 oilAmt = String.valueOf(oilAmtInt);
		 //统计
		 incomeAmtInt = adjustAmtInt + addOilSrvAmtInt + fineAmtInt + rentAmtInt + beyondMileAmtInt + oilAmtInt;
		 incomeAmt = String.valueOf(incomeAmtInt);
		 
		 realVo.setIncomeAmt(incomeAmt);
		 realVo.setRentAmt(rentAmt);
		 realVo.setFineAmt(fineAmt);
		 realVo.setAdjustAmt(adjustAmt);
		 realVo.setBeyondMileAmt(beyondMileAmt);
		 realVo.setOilAmt(oilAmt);
		 realVo.setAddOilSrvAmt(addOilSrvAmt);
		 
	}

	private void putPlatformDeductAmt(OrderOwnerCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		 String platformDeductionAmt = "0";
		 
		 String platformSrvFeeAmt = "0";
		 String platformAddOilSrvAmt = "0";
		 String ownerPayToPlatform = "0";
		 String gpsAmt = "0";
		 String gpsDeposit = "0";
		 String carServiceSrvFee = "0"; 
		
		 int srvFee = 0;
		 int oil = 0;
		 int ownerPay = 0;  //默认
		 int gps = 0;
		 int gpsDepositAmt = 0;  //默认
		 int getReturnCarFee = 0;  //配送,增值订单
		 
		 OwnerOrderPurchaseDetailEntity proxyExpense = data.getProxyExpense();
		 if(proxyExpense != null) {
			 srvFee = proxyExpense.getTotalAmount();
		 }
	    /**
	     * 车主端平台服务费
	     */
	     OwnerOrderPurchaseDetailEntity serviceExpense = data.getServiceExpense();
	     if(serviceExpense != null) {
	    	 srvFee += serviceExpense.getTotalAmount();
	     }
	     
	     /**
	      * 加油服务费
	      */
	     OwnerOrderPurchaseDetailEntity ownerOrderCostDetail = data.getOwnerOrderCostDetail();
	     if(ownerOrderCostDetail != null) {
	    	 oil += ownerOrderCostDetail.getTotalAmount().intValue();
	     }
	     /**
	      * 获取gps服务费
	      */
	      List<OwnerOrderPurchaseDetailEntity> gpsCost = data.getGpsCost();
	      if(gpsCost != null) {
	    	  for (OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity : gpsCost) {
	    		  gps += ownerOrderPurchaseDetailEntity.getTotalAmount().intValue();
	    	  }
		  }
	      
	      /**
	       * 获取车主增值服务费用列表
	       */
	       List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = data.getOwnerOrderIncrementDetail();
	       if(ownerOrderIncrementDetail != null) {
	    	   for (OwnerOrderIncrementDetailEntity ownerOrderIncrementDetailEntity : ownerOrderIncrementDetail) {
	    		   if(ownerOrderIncrementDetailEntity.getCostCode().equals(OwnerCashCodeEnum.SRV_GET_COST_OWNER.getCashNo())) {
	    			   getReturnCarFee += ownerOrderIncrementDetailEntity.getTotalAmount().intValue();
	    		   }else if(ownerOrderIncrementDetailEntity.getCostCode().equals(OwnerCashCodeEnum.SRV_RETURN_COST_OWNER.getCashNo())) {
	    			   getReturnCarFee += ownerOrderIncrementDetailEntity.getTotalAmount().intValue();
	    		   }
	    	   }
	       }
	 
		 
		 
		 int total = srvFee + oil + ownerPay + gps + gpsDepositAmt + getReturnCarFee;
		 platformDeductionAmt = String.valueOf(total);
		 platformSrvFeeAmt = String.valueOf(srvFee);
		 platformAddOilSrvAmt = String.valueOf(oil);
		 ownerPayToPlatform = String.valueOf(ownerPay);
		 gpsAmt = String.valueOf(gps);
		 gpsDeposit = String.valueOf(gpsDepositAmt);
		 carServiceSrvFee = String.valueOf(getReturnCarFee);
		 
		 //封装
		 realVo.setPlatformDeductionAmt(platformDeductionAmt);
		 realVo.setPlatformSrvFeeAmt(platformSrvFeeAmt);
		 realVo.setPlatformAddOilSrvAmt(platformAddOilSrvAmt);
		 realVo.setOwnerPayToPlatform(ownerPayToPlatform);
		 realVo.setGpsAmt(gpsAmt);
		 realVo.setGpsDeposit(gpsDeposit);  //默认处理
		 realVo.setCarServiceSrvFee(carServiceSrvFee);
		 
	}

	private void putPlatformSubsidyAndOwnerCoupon(OrderOwnerCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		 String couponDeductionAmount = "0";
		 String ownerCouponTitle = ""; 
		 String ownerCouponAmt = "0";
		 String ownerSubsidyRentAmt = "0";
		 //补贴
		 String platformSubsidyTotalAmt = "0";
		 String platformSubsidyAmt = "0";
		 
		 int platformSubsidyAmount = 0;
		 int ownerSubsidyRentAmount = 0;
		 List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = data.getOrderConsoleSubsidyDetails();
			for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : orderConsoleSubsidyDetails) {
				//`subsidy_source_name` varchar(16) DEFAULT NULL COMMENT '补贴来源方 1、租客 2、车主 3、平台',
				//  `subsidy_target_name` varchar(16) DEFAULT NULL COMMENT '补贴方名称 1、租客 2、车主 3、平台',
				if("3".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceName()) && "2".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetName())) {
					platformSubsidyAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
				
				if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceName()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetName())) {
					//租金的补贴
					if(orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo())) {
						ownerSubsidyRentAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
					}
				}
				
			}
		 
			
			int ownerCouponAmtInt = 0;
			List<OrderCouponEntity> orderCouponList = data.getOrderCouponList();
			for (OrderCouponEntity orderCouponEntity : orderCouponList) {
				if(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode() == orderCouponEntity.getCouponType().intValue()) {
					ownerCouponTitle = orderCouponEntity.getCouponName();
					ownerCouponAmtInt +=  orderCouponEntity.getAmount();
				}
			}
			
			
			platformSubsidyTotalAmt = String.valueOf(platformSubsidyAmount);	
			platformSubsidyAmt = String.valueOf(platformSubsidyAmount);	
			ownerCouponAmt = String.valueOf(ownerCouponAmtInt);
			couponDeductionAmount = String.valueOf(ownerCouponAmtInt + ownerSubsidyRentAmount);
			ownerSubsidyRentAmt = String.valueOf(ownerSubsidyRentAmount);
			
		 //抵扣  给租客的优惠
		 realVo.setCouponDeductionAmount(couponDeductionAmount);
		 realVo.setOwnerCouponTitle(ownerCouponTitle);
		 realVo.setOwnerCouponAmt(ownerCouponAmt);
		 realVo.setOwnerSubsidyRentAmt(ownerSubsidyRentAmt);
		 /////平台补贴
		 realVo.setPlatformSubsidyAmt(platformSubsidyAmt);
		 realVo.setPlatformSubsidyTotalAmt(platformSubsidyTotalAmt);
	}
	
}
