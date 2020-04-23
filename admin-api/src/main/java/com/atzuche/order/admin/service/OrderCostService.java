/**
 * 
 */
package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.req.cost.OwnerCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderOwnerCostResVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderRenterCostResVO;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.coin.service.AutoCoinProxyService;
import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.entity.dto.OwnerCouponLongDTO;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.cashcode.FineTypeCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.ownercosts.*;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.open.service.FeignOrderCostService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderIncrementDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.utils.OrderSubsidyDetailUtils;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.res.RenterCostVO;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	@Autowired
	OwnerOrderService ownerOrderService;
	@Autowired
	RenterOrderService renterOrderService;
	@Autowired
    private WalletProxyService walletProxyService;
	@Autowired
	private AutoCoinProxyService autoCoinProxyService;
	@Autowired
	private CashierPayService cashierPayService;
	@Autowired
	private OrderSettleService orderSettleService;
	@Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
	@Autowired
    private AdminDeliveryCarService deliveryCarInfoService;
	@Autowired
	private DeliveryCarInfoPriceService deliveryCarInfoPriceService;
	
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
//		realVo.setAdjustAmt("0");
		//加油服务费 huangjing-todo
		realVo.setAddOilSrvAmt("0");
		///租客需支付给平台的费用 huangjing-todo  -DO
//		realVo.setRenterPayToPlatform("0");
		
		ResponseData<com.atzuche.order.commons.vo.res.OrderRenterCostResVO> resData = feignOrderCostService.orderCostRenterGet(req);
		if(resData != null) {
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data = resData.getData();
			if(data != null) {
				int renterCostAmtFinal = data.getRenterCostAmtFinal();
				RenterCostVO costVo = orderSettleService.getRenterCostByOrderNo(renterCostReqVO.getOrderNo(),renterCostReqVO.getRenterOrderNo(),orderEntity.getMemNoRenter(),renterCostAmtFinal);
				if(costVo != null) {
					 logger.info("costVo toString=[{}]",costVo.toString());
				}
				
				//租金费用  费用明细表renter_order_cost_detail   
				putRenterOrderCostDetail(realVo,data);
                //平台保障费、全面保障服务费 （长租不需要这两个费用）
                putInsureAbatementAmt(realVo,data);
				//租客订单对象
				RenterOrderEntity entity = renterOrderService.getRenterOrderByRenterOrderNo(renterCostReqVO.getRenterOrderNo());
				//优惠抵扣  优惠抵扣 券，凹凸币，钱包   钱包抵扣
				putRenterOrderDeduct(realVo,data,entity,orderEntity.getMemNoRenter());
				
				//租车押金和违章押金   车辆押金  违章押金
				putRenterOrderDeposit(realVo,data,costVo);
				
				//取还车服务违约金   renter_order_fine_deatail  取还车服务违约金
				putRenterOrderFine(realVo,data);
				
				//租车费用应收@海豹   实收
				putPaymentAmount(realVo,data,costVo);
				
				//油费,超里程
				putOilBeyondMile(realVo,data);
				
				//平台给租客的补贴， 车主和租客的调价，车主给租客的租金补贴
				putConsoleSubsidy(realVo,data);
				
				//补付费用 
				//需补付金额,跟修改订单的补付金额是同一个方法。
//				int needIncrementAmt = cashierPayService.getRentCostBufuNew(renterCostReqVO.getOrderNo(), orderEntity.getMemNoRenter());
				putSupplementAmt(realVo,data,costVo);
				
				//租客支付给平台的费用。console  200214
				putRenterToPlatformCost(realVo,data);
				
				//rentFeeBase基础费用
				putRentFeeBase(realVo,data);
			}
		}
		
		return realVo;
	}
	
	/*
	 * 租客支付给平台的费用
	 */
	private void putRenterToPlatformCost(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		int renterPayToPlatformAmt = 0;
		
		List<OrderConsoleCostDetailEntity> costDetails = data.getOrderConsoleCostDetails();
		for (OrderConsoleCostDetailEntity entity : costDetails) {
			//`subsidy_source_name` varchar(16) DEFAULT NULL COMMENT '补贴来源方 1、租客 2、车主 3、平台',
			//  `subsidy_target_name` varchar(16) DEFAULT NULL COMMENT '补贴方名称 1、租客 2、车主 3、平台',
			if("1".equals(entity.getSubsidySourceCode()) && "3".equals(entity.getSubsidyTargetCode())) {
				renterPayToPlatformAmt += entity.getSubsidyAmount().intValue();
			}
		}
		
		//数据封装
		realVo.setRenterPayToPlatform(String.valueOf(NumberUtils.convertNumberToZhengshu(renterPayToPlatformAmt)));
	}
	
	/*
	 * 车主支付给平台的费用
	 */
	private void putOwnerToPlatformCost(OrderOwnerCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		int ownerPayToPlatformAmt = 0;
		
		List<OrderConsoleCostDetailEntity> costDetails = data.getOrderConsoleCostDetails();
		for (OrderConsoleCostDetailEntity entity : costDetails) {
			//`subsidy_source_name` varchar(16) DEFAULT NULL COMMENT '补贴来源方 1、租客 2、车主 3、平台',
			//  `subsidy_target_name` varchar(16) DEFAULT NULL COMMENT '补贴方名称 1、租客 2、车主 3、平台',
			if("2".equals(entity.getSubsidySourceCode()) && "3".equals(entity.getSubsidyTargetCode())) {
				ownerPayToPlatformAmt += entity.getSubsidyAmount().intValue();
			}
		}
		
		//数据封装
		realVo.setOwnerPayToPlatform(String.valueOf(NumberUtils.convertNumberToFushu(ownerPayToPlatformAmt)));
	}

	private void putRentFeeBase(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		//租客租金
		int rentAmt = Integer.valueOf(realVo.getRentAmount()==null?"0":realVo.getRentAmount());
		//基础保障费
		int insure = Integer.valueOf(realVo.getInsuranceAmount()==null?"0":realVo.getInsuranceAmount());
//		附加驾驶员险
		int drive = Integer.valueOf(realVo.getAdditionalDriverInsuranceAmount()==null?"0":realVo.getAdditionalDriverInsuranceAmount());
        //违约罚金
        int fine = Integer.valueOf(realVo.getCarServiceFine()==null?"0":realVo.getCarServiceFine());
        //调价
        int adjust = Integer.valueOf(realVo.getAdjustAmt()==null?"0":realVo.getAdjustAmt());
        //油费
        int oil = Integer.valueOf(realVo.getOilAmt()==null?"0":realVo.getOilAmt());
        //手续费
        int fee = Integer.valueOf(realVo.getServiceCharge()==null?"0":realVo.getServiceCharge());
        //全面保障
        int insureAll = Integer.valueOf(realVo.getSupperInsuranceAmount()==null?"0":realVo.getSupperInsuranceAmount());
        //配送
        int getReturnFee = Integer.valueOf(realVo.getCarServiceFee()==null?"0":realVo.getCarServiceFee());
        //租客需支付给平台的费用
        int rentPayToPlatform = Integer.valueOf(realVo.getRenterPayToPlatform()==null?"0":realVo.getRenterPayToPlatform());
		//超里程
		int beyondMile = StringUtils.isNotBlank(realVo.getBeyondMileAmt())?Integer.valueOf(realVo.getBeyondMileAmt()):0;
		//加油服务费
		int addOil = Integer.valueOf(realVo.getAddOilSrvAmt()==null?"0":realVo.getAddOilSrvAmt());
		
		int base = rentAmt + insure + drive + fine + adjust + oil + fee + insureAll + getReturnFee + rentPayToPlatform + beyondMile + addOil;
		
		//取反，显示为正数，因为是从realVo中获取，已经转义过了。
		realVo.setRentFeeBase(String.valueOf(NumberUtils.convertNumberToZhengshu(base)));
	}

	private void putSupplementAmt(OrderRenterCostResVO realVo,com.atzuche.order.commons.vo.res.OrderRenterCostResVO data, RenterCostVO costVo) {
		String paymentAmountShishou = "";
		String paymentAmountYingshou = "";
		int yingshouAmt = costVo.getRenterCostBufuYingshou();
		int shishouAmt = costVo.getRenterCostBufuShishou();  //补付   实收
		
		/*
		 * List<OrderSupplementDetailEntity> supplementList = data.getSupplementList();
		 * for (OrderSupplementDetailEntity orderSupplementDetailEntity :
		 * supplementList) { // yingshouAmt +=
		 * orderSupplementDetailEntity.getAmt().intValue();
		 * if(orderSupplementDetailEntity.getPayFlag() == 0 ||
		 * orderSupplementDetailEntity.getPayFlag() == 3 ||
		 * orderSupplementDetailEntity.getPayFlag() == 10 ||
		 * orderSupplementDetailEntity.getPayFlag() == 20) { shishouAmt +=
		 * orderSupplementDetailEntity.getAmt().intValue(); } }
		 */
		
		//这个是管理后台的补付
//		paymentAmountShishou = String.valueOf( NumberUtils.convertNumberToZhengshu(shishouAmt));
//		paymentAmountYingshou = String.valueOf( NumberUtils.convertNumberToZhengshu(yingshouAmt));
		

		//海豹提供  
		paymentAmountShishou = String.valueOf(Math.abs(shishouAmt)); //NumberUtils.convertNumberToZhengshu(shishouAmt)
		//总额补付
		paymentAmountYingshou = String.valueOf(yingshouAmt>=0?0:NumberUtils.convertNumberToZhengshu(yingshouAmt));
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
		
		//租客给车主的调价
	    //租客给车主的调价
		int renterToOwnerAdjustAmount = 0;
		
		List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = data.getOrderConsoleSubsidyDetails();

        List<RenterOrderSubsidyDetailResVO> subsidyLst1 = data.getSubsidyLst();
        for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : orderConsoleSubsidyDetails) {
			//`subsidy_source_name` varchar(16) DEFAULT NULL COMMENT '补贴来源方 1、租客 2、车主 3、平台',
			//  `subsidy_target_name` varchar(16) DEFAULT NULL COMMENT '补贴方名称 1、租客 2、车主 3、平台',
			if("3".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())) {
				platformSubsidyAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
			}
//			1111
			///  从租客补贴表中获取，如下:租客补贴  20200205
//			if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceName()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetName())) {
//				//车主给租客的补贴
//				ownerSubsidyRentTotalAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
//				//租金的补贴
//				if(orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo())) {
//					ownerSubsidyRentAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
//				}
//			}
			
		}
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList = new ArrayList<>();
        subsidyLst1.forEach(x->{
            RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity = new RenterOrderSubsidyDetailEntity();
            BeanUtils.copyProperties(x,renterOrderSubsidyDetailEntity);
            renterOrderSubsidyDetailEntityList.add(renterOrderSubsidyDetailEntity);
        });
        int renterUpateSubsidyAmt = OrderSubsidyDetailUtils.getRenterUpateSubsidyAmt(renterOrderSubsidyDetailEntityList);
        int renterSubsidyAmt = OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.INSURE_TOTAL_PRICES);
        int abatementInsureAmt = OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.ABATEMENT_INSURE);
        platformSubsidyAmount += (renterUpateSubsidyAmt+renterSubsidyAmt+abatementInsureAmt);


		for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : orderConsoleSubsidyDetails) {
			//补贴来源方 1、租客 2、车主 3、平台
			//补贴方名称 1、租客 2、车主 3、平台
			//租客给车主的调价
			if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
				if(RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					renterToOwnerAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
			}else if("1".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "2".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
				if(RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					//不需要累计，只是查询记录
					renterToOwnerAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
			}
			
		}
		
		/**
		 * 租客补贴 20200205
		 */
		List<RenterOrderSubsidyDetailResVO> subsidyLst = subsidyLst1;
		for (RenterOrderSubsidyDetailResVO renterOrderSubsidyDetailResVO : subsidyLst) {
			
			if("2".equals(renterOrderSubsidyDetailResVO.getSubsidySourceCode()) && "1".equals(renterOrderSubsidyDetailResVO.getSubsidyTargetCode())) {
				//车主给租客的补贴
				ownerSubsidyRentTotalAmount += renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
				//租金的补贴
				if(renterOrderSubsidyDetailResVO.getSubsidyCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo())) {
					ownerSubsidyRentAmount += renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
				}
			}
		}
		
		
		
		platformSubsidyTotalAmt = String.valueOf( NumberUtils.convertNumberToFushu(platformSubsidyAmount));
		platformSubsidyAmt = String.valueOf( NumberUtils.convertNumberToFushu(platformSubsidyAmount));
		platformSubsidyRealAmt = String.valueOf( NumberUtils.convertNumberToFushu(platformSubsidyAmount));
		///车主给租客的租金补贴
		ownerSubsidyTotalAmt = String.valueOf( NumberUtils.convertNumberToFushu(ownerSubsidyRentTotalAmount));
		//文本框中填入负数，保存负数，暂不处理
//		ownerSubsidyRentAmt = String.valueOf( ownerSubsidyRentAmount);  //NumberUtils.convertNumberToFushu()
		ownerSubsidyRentAmt = String.valueOf( NumberUtils.convertNumberToFushu(ownerSubsidyRentAmount));
		
		realVo.setPlatformSubsidyTotalAmt(platformSubsidyTotalAmt);
		realVo.setPlatformSubsidyRealAmt(platformSubsidyRealAmt);
		realVo.setPlatformSubsidyAmt(platformSubsidyAmt);
		///
		realVo.setOwnerSubsidyTotalAmt(ownerSubsidyTotalAmt);
		realVo.setOwnerSubsidyRentAmt(ownerSubsidyRentAmt);
		//租客给车主的调价 20200211
		//bugfix:AUT-5118    取反，租客的调价为“显示正数” 租客的调价收益 “显示负数”
		realVo.setAdjustAmt(String.valueOf( -renterToOwnerAdjustAmount)); //NumberUtils.convertNumberToZhengshu(renterToOwnerAdjustAmount)
		
	}
	
	private void putOilBeyondMile(OrderRenterCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		int oilAmt = data.getOilAmt();
		realVo.setOilAmt(String.valueOf( NumberUtils.convertNumberToZhengshu(oilAmt)));

		int mileageAmt = data.getMileageAmt();
		realVo.setBeyondMileAmt(String.valueOf( NumberUtils.convertNumberToZhengshu(mileageAmt)));
	}

	private void putRenterOrderFine(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		int carServiceFine = 0;
		List<RenterOrderFineDeatailResVO> fineLst = data.getFineLst();
		/*console_renter_order_fine_deatail    renter_order_fine_deatail  来源是租客的，累加求和*/
		for (RenterOrderFineDeatailResVO renterOrderFineDeatailResVO : fineLst) {
			//fine_type  罚金类型：1-修改订单取车违约金，2-修改订单还车违约金
//			if(renterOrderFineDeatailResVO.getFineType().intValue() == 1) {
//				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
//			}
//			if(renterOrderFineDeatailResVO.getFineType().intValue() == 2) {
//				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
//			}
			
			if("1".equals(renterOrderFineDeatailResVO.getFineSubsidySourceCode())) {
				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
			}
			
		}
		
		//从管理后台求和。 20200212
		List<com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity> consoleFineLst = data.getConsoleFineLst();
		for (com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity : consoleFineLst) {
			if("1".equals(consoleRenterOrderFineDeatailEntity.getFineSubsidySourceCode())) {
				carServiceFine += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}
		}
		
		
		//违约罚金 显示求反，显示正数。
		realVo.setCarServiceFine(String.valueOf(NumberUtils.convertNumberToZhengshu(carServiceFine)));
	}
	
	

	
	private void putRenterOrderDeposit(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data, RenterCostVO costVo) {
	    realVo.setVehicleDeposit(data.getRentVo()!=null && data.getRentVo().getOriginalDepositAmt() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getRentVo().getOriginalDepositAmt())):"---");
		realVo.setViolationDeposit(data.getWzVo()!=null && data.getWzVo().getYingshouDeposit() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getWzVo().getYingshouDeposit())):"---");
		
		//任务减免金额
		realVo.setPlatformTaskFreeAmt(data.getRentVo()!=null && data.getRentVo().getReductionAmt() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getRentVo().getReductionAmt())):"---");
				
				
		//
//		realVo.setVehicleDepositYingshou(data.getRentVo()!=null && data.getRentVo().getYingfuDepositAmt() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getRentVo().getYingfuDepositAmt())):"---");
//		realVo.setVehicleDepositShishou(data.getRentVo()!=null && data.getRentVo().getShifuDepositAmt() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getRentVo().getShifuDepositAmt())):"---");
		//海豹后面表里要加上的。 海豹的表里面有的 huangjing-todo
//		realVo.setVehicleDepositYingtui("---"); //data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---"
//		realVo.setVehicleDepositShitui("---"); //data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---"
		
		//
//		realVo.setViolationDepositYingshou(data.getWzVo()!=null && data.getWzVo().getYingshouDeposit() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getWzVo().getYingshouDeposit())):"---");
//		realVo.setViolationDepositShishou(data.getWzVo()!=null && data.getWzVo().getShishouDeposit() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(data.getWzVo().getShishouDeposit())):"---");
		//海豹后面表里要加上的。 海豹的表里面有的 huangjing-todo
//		realVo.setViolationDepositYingtui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
//		realVo.setViolationDepositShitui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
		
		
		//直接从海豹那边的调用
		realVo.setVehicleDepositYingshou(String.valueOf(costVo.getDepositCostYingshou()));
		realVo.setVehicleDepositShishou(String.valueOf(costVo.getDepositCostShishou()));
		realVo.setVehicleDepositYingtui(String.valueOf(costVo.getDepositCostYingtui()));
		realVo.setVehicleDepositShitui(String.valueOf(costVo.getDepositCostShitui()));
		//add
		realVo.setVehicleDepositYingkou(String.valueOf(costVo.getDepositCostYingkou()));
		realVo.setVehicleDepositShikou(String.valueOf(costVo.getDepositCostShikou()));
		//add 200416
		realVo.setVehicleDepositShishouAuth(String.valueOf(costVo.getDepositCostShishouAuth()));
		
		
		//直接从海豹那边的调用
		realVo.setViolationDepositYingshou(String.valueOf(costVo.getDepositWzCostYingshou()));
		realVo.setViolationDepositShishou(String.valueOf(costVo.getDepositWzCostShishou()));
		realVo.setViolationDepositYingtui(String.valueOf(costVo.getDepositWzCostYingtui()));
		realVo.setViolationDepositShitui(String.valueOf(costVo.getDepositWzCostShitui()));
		//add
		realVo.setViolationDepositYingkou(String.valueOf(costVo.getDepositWzCostYingkou()));
		realVo.setViolationDepositShikou(String.valueOf(costVo.getDepositWzCostShikou()));
		//add 200416
		realVo.setViolationDepositShishouAuth(String.valueOf(costVo.getDepositWzCostShishouAuth()));
		
	}

	private void putRenterOrderDeduct(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data, RenterOrderEntity entity, String memNo) {
		int walletAmt = data.getWalletCostDetail().getAmt();
		//租客的补贴
		//还需要考虑加上管理后台的补贴
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
		
		/*负数表示*/
		realVo.setDeductionAmount(String.valueOf(NumberUtils.convertNumberToFushu(deductionAmount)));
		//车主券
		realVo.setOwnerCouponAmt(String.valueOf(NumberUtils.convertNumberToFushu(ownerCoupon)));
		//平台券
		realVo.setPlatformCouponAmt(String.valueOf(NumberUtils.convertNumberToFushu(platformCoupon)));
		//送取服务券
		realVo.setGetReturnCouponAmt(String.valueOf(NumberUtils.convertNumberToFushu(getReturnCoupon)));
		//钱包余额
		realVo.setWalletAmt(String.valueOf(NumberUtils.convertNumberToFushu(walletAmt)));
		//凹凸币
		realVo.setAotuCoinAmt(String.valueOf(NumberUtils.convertNumberToFushu(aotuCoin)));
		//钱包余额	暂不处理  下单的接口  huangjing-todo do
		int walletAmtLeft = walletProxyService.getWalletByMemNo(memNo);
		realVo.setWalletTotalAmt(String.valueOf(walletAmtLeft));
		//凹凸币余额   暂不处理 下单的接口 huangjing-todo do
		int autoCoinLeft = autoCoinProxyService.getCrmCustPoint(memNo);
		realVo.setAotuCoinTotalAmt(String.valueOf(autoCoinLeft));
		/*
		 * 获取标题
		 */
		String platformCouponTitle = "";
		String ownerCouponTitle = "";
		String getReturnCouponTitle = ""; 
		List<OrderCouponEntity> orderCouponList = data.getOrderCouponList();
		for (OrderCouponEntity orderCouponEntity : orderCouponList) {
			if(orderCouponEntity != null){
				if(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode() == orderCouponEntity.getCouponType().intValue()) {
					getReturnCouponTitle = orderCouponEntity.getCouponName();
				}else if(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode() == orderCouponEntity.getCouponType().intValue()) {
					ownerCouponTitle = orderCouponEntity.getCouponName();
				}else if(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode() == orderCouponEntity.getCouponType().intValue()) {
					platformCouponTitle = orderCouponEntity.getCouponName();
				}
			}
		}
		//数据标题
		realVo.setPlatformCouponTitle(platformCouponTitle);
		realVo.setOwnerCouponTitle(ownerCouponTitle);
		realVo.setGetReturnCouponTitle(getReturnCouponTitle);
		
		//是否使用标识处理
		if(entity != null) {
			realVo.setIsUseAotuCoin(String.valueOf(entity.getIsUseCoin()));
			realVo.setIsUseGetReturnCoupon(getReturnCoupon>0?"1":"0");  //0否1是
			realVo.setIsUseWallet(String.valueOf(entity.getIsUseWallet()));
		}
	}

    private void longRentDeduct(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
        List<RenterOrderSubsidyDetailResVO> subsidyLst = data.getSubsidyLst();
        int sum = Optional.ofNullable(subsidyLst)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> RenterCashCodeEnum.RENT_AMT.getCashNo().equals(x.getSubsidyCostCode()))
                .mapToInt(RenterOrderSubsidyDetailResVO::getSubsidyAmount)
                .sum();
        int deductionAmount = realVo.getDeductionAmount() != null ? 0 : Integer.valueOf(realVo.getDeductionAmount());
        deductionAmount = deductionAmount + (-sum);
        realVo.setDeductionAmount(String.valueOf(NumberUtils.convertNumberToFushu(deductionAmount)));

        OwnerCouponLongDTO ownerCouponLongDTO = data.getOwnerCouponLongDTO();
        if(ownerCouponLongDTO != null){
            realVo.setOwnerLongRentDeduct(ownerCouponLongDTO.getDiscountDesc());
        }
        realVo.setOwnerLongRentDeductAmt(String.valueOf(NumberUtils.convertNumberToFushu(sum)));
    }


    private void putInsureAbatementAmt(OrderRenterCostResVO realVo,com.atzuche.order.commons.vo.res.OrderRenterCostResVO data){
        //平台保障费
        String insureCashNo = RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo();
        //全面保障服务费
        String abatementCashNo = RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo();
        //费用列表
        List<RenterOrderCostDetailResVO> costList = data.getRenterOrderCostDetailList();
        int insuranceAmount = 0;
        int supperInsuranceAmount = 0;
        if(costList != null) {
            for (RenterOrderCostDetailResVO renterOrderCostDetailResVO : costList) {
                 if(insureCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
                    insuranceAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();

                }else if(abatementCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
                    supperInsuranceAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();

                }
            }
        }
        //基础保障费
        realVo.setInsuranceAmount(String.valueOf(NumberUtils.convertNumberToZhengshu(insuranceAmount)));
        //全面保障服务费
        realVo.setSupperInsuranceAmount(String.valueOf(NumberUtils.convertNumberToZhengshu(supperInsuranceAmount)));
    }

	private void putRenterOrderCostDetail(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data) {
		/**
		 * 
		 */
		//租客租金
		String rentAmtCashNo =  RenterCashCodeEnum.RENT_AMT.getCashNo();

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

		int additionalDriverInsuranceAmount = 0;
		int serviceCharge = 0;
		int carServiceFee = 0;
		
		//费用列表
		List<RenterOrderCostDetailResVO> costList = data.getRenterOrderCostDetailList();
		if(costList != null) {
			for (RenterOrderCostDetailResVO renterOrderCostDetailResVO : costList) {
				if(rentAmtCashNo.equals(renterOrderCostDetailResVO.getCostCode())) {
					rentAmount +=  renterOrderCostDetailResVO.getTotalAmount().intValue();
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
		realVo.setRentAmount(String.valueOf(NumberUtils.convertNumberToZhengshu(rentAmount)));
		//附加驾驶员险
		realVo.setAdditionalDriverInsuranceAmount(String.valueOf(NumberUtils.convertNumberToZhengshu(additionalDriverInsuranceAmount)));
		//手续费
		realVo.setServiceCharge(String.valueOf(NumberUtils.convertNumberToZhengshu(serviceCharge)));
		//配送费用
		realVo.setCarServiceFee(String.valueOf(NumberUtils.convertNumberToZhengshu(carServiceFee)));
		
	}

	/**
	 * 租客 租车费用
	 * @param realVo
	 * @param data
	 * @param costVo 
	 */
	private void putPaymentAmount(OrderRenterCostResVO realVo, com.atzuche.order.commons.vo.res.OrderRenterCostResVO data, RenterCostVO costVo) {
		//应收。
//		realVo.setRentFeeYingshou(String.valueOf(NumberUtils.convertNumberToZhengshu(data.getNeedIncrementAmt())));
		//实收
//		AccountRenterCostSettleResVO renterSettleVo = data.getRenterSettleVo();
//		realVo.setRentFeeShishou(renterSettleVo!=null && renterSettleVo.getShifuAmt() != null?String.valueOf(NumberUtils.convertNumberToZhengshu(renterSettleVo.getShifuAmt())):"---");
		//海豹后面表里要加上的。 海豹的表里面有的 huangjing-todo
//		realVo.setRentFeeYingtui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
//		realVo.setRentFeeShitui("---");//data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---"
		
		//直接从海豹那边的调用
		realVo.setRentFeeYingshou(String.valueOf(costVo.getRenterCostFeeYingshou()));
		realVo.setRentFeeShishou(String.valueOf(costVo.getRenterCostFeeShishou()));
		realVo.setRentFeeYingtui(String.valueOf(costVo.getRenterCostFeeYingtui()));
		realVo.setRentFeeShitui(String.valueOf(costVo.getRenterCostFeeShitui()));
		//add
		realVo.setRentFeeYingkou(String.valueOf(costVo.getRenterCostFeeYingkou()));
		realVo.setRentFeeShikou(String.valueOf(costVo.getRenterCostFeeShikou()));
	}
	
	
	/**
	 * 
	 * @param ownerCostReqVO
	 * @return
	 * @throws Exception 
	 */
	public OrderOwnerCostResVO calculateOwnerOrderCost(OwnerCostReqVO ownerCostReqVO) throws Exception {
		OrderOwnerCostResVO realVo = new OrderOwnerCostResVO();
		
		OwnerOrderEntity orderEntityOwner = null;  
	    if(StringUtils.isNotBlank(ownerCostReqVO.getOwnerOrderNo())) {  
		    orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
	        if(orderEntityOwner == null){
	        	logger.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
	            throw new Exception("获取订单数据(车主)为空");
	        }
	    }
//	    else {
//	    	//否则根据主订单号查询
//	    	orderEntityOwner = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(ownerCostReqVO.getOrderNo());
//	    	
//	    }
	    
		OrderCostReqVO req = new OrderCostReqVO();
		req.setOrderNo(ownerCostReqVO.getOrderNo());
		req.setSubOrderNo(ownerCostReqVO.getOwnerOrderNo());
		req.setMemNo(orderEntityOwner.getMemNo());
		ResponseData<com.atzuche.order.commons.vo.res.OrderOwnerCostResVO> resData = feignOrderCostService.orderCostOwnerGet(req);
		
		//子订单号
		realVo.setOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
		//默认值处理  调价目前没有
//		realVo.setAdjustAmt("0");
		//加油服务费
		realVo.setAddOilSrvAmt("0");
		///车主需支付给平台的费用
//		realVo.setOwnerPayToPlatform("0");
		
		
		if(resData != null) {
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data = resData.getData();
			if(data != null) {
				//租客支付给平台的费用。console  200214
				putOwnerToPlatformCost(realVo,data);
				
				//给租客的优惠（车主券）和平台补贴   调价
				putPlatformSubsidyAndOwnerCoupon(realVo,data);

				//扣款
				putPlatformDeductAmt(realVo,data);
				
				//收益
				putBaseFee(realVo,data,ownerCostReqVO);
				
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
//		 String preIncomeAmt = "0";
		 //结算收益 huangjingtodo     取account_owner_income_examine表
//		 String settleIncomeAmt = "0";  //最终收益 income_amt （海豹要加上）
		 
		 int preIncomeAmtInt = data.getOwnerCostAmtFinal(); //直接取值。
		 //是从结算表中求和获取。
		 int settleIncomeAmtInt = data.getOwnerCostAmtSettleAfter();
//		 int incomeAmt = Integer.valueOf(realVo.getIncomeAmt());
//		 int platformDeductionAmt = Integer.valueOf(realVo.getPlatformDeductionAmt());
//		 int couponDeductionAmount = Integer.valueOf(realVo.getCouponDeductionAmount());
//		 int platformSubsidyTotalAmt = Integer.valueOf(realVo.getPlatformSubsidyTotalAmt());
//		 preIncomeAmtInt = incomeAmt + platformDeductionAmt + couponDeductionAmount + platformSubsidyTotalAmt;
		 
//		 preIncomeAmt = String.valueOf(NumberUtils.convertNumberToZhengshu(preIncomeAmtInt));
		 realVo.setPreIncomeAmt(String.valueOf(preIncomeAmtInt));
		 realVo.setSettleIncomeAmt(String.valueOf(settleIncomeAmtInt));
		 
	}

	private void putBaseFee(OrderOwnerCostResVO realVo, com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data,OwnerCostReqVO ownerCostReqVO) {
		//收益
		String incomeAmt;
		
		//车主租金
		 String rentAmt;

		//违约罚金
		 String fineAmt;

		//租客车主互相调价
//		 String adjustAmt = "0"; //默认
		
		//超里程费用
		 String beyondMileAmt = "0";  //
		
		//油费费用
		 String oilAmt = "0";  //
		
		//加油服务费用
		 String addOilSrvAmt = "0";  //海豹未结算? huangjingtodo
		 
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
				 //去掉该条件，修改订单提前还车违约金 该类是租客给车主的罚金。 200304
//				if(ownerOrderFineDeatailEntity.getFineSubsidySourceCode().equals("2")) {
					fineAmtInt += ownerOrderFineDeatailEntity.getFineAmount().intValue();
//				}   
			}
		 }
		 
		 //求和
		 List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatails = data.getConsoleOwnerOrderFineDeatails();
		 for (ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity : consoleOwnerOrderFineDeatails) {
			 //罚金来源编码（车主/租客/平台）1-租客，2-车主，3-平台
				if(FineTypeCashCodeEnum.MODIFY_ADDRESS_FINE.getFineType().equals(consoleOwnerOrderFineDeatailEntity.getFineType())) {
					fineAmtInt += consoleOwnerOrderFineDeatailEntity.getFineAmount().intValue();
				}
		 }
		 
		 //存在正负号。
		 fineAmt = String.valueOf( fineAmtInt);  //NumberUtils.convertNumberToZhengshu()
		 
		 /**
		     * 获取车主费用列表
		     */
		 List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = data.getOwnerOrderPurchaseDetail();
		 if(ownerOrderPurchaseDetail != null) {
			 for (OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity : ownerOrderPurchaseDetail) {
				 if(ownerOrderPurchaseDetailEntity.getCostCode().equals(OwnerCashCodeEnum.RENT_AMT.getCashNo())) {
					 rentAmtInt += ownerOrderPurchaseDetailEntity.getTotalAmount().intValue();
	    		  }
			 }
		 }

		 rentAmt = String.valueOf( NumberUtils.convertNumberToZhengshu(rentAmtInt));
		 
         oilAmtInt = data.getOwnerOilDifferenceCrashAmt();
		 beyondMileAmtInt = data.getMileageAmt();
         //车主油费和超里程费用，是可正可负。
		 beyondMileAmt = String.valueOf(Math.abs(beyondMileAmtInt));
		 oilAmt = String.valueOf(oilAmtInt);
		 
		 //统计
		 incomeAmtInt = adjustAmtInt + addOilSrvAmtInt + fineAmtInt + rentAmtInt + beyondMileAmtInt + oilAmtInt;
		 incomeAmt = String.valueOf( NumberUtils.convertNumberToZhengshu(incomeAmtInt));
		 
		 realVo.setIncomeAmt(incomeAmt);
		 realVo.setRentAmt(rentAmt);
		 realVo.setFineAmt(fineAmt);
		 //值被覆盖了。。。
//		 realVo.setAdjustAmt(adjustAmt);
		 realVo.setBeyondMileAmt(beyondMileAmt);
		 realVo.setOilAmt(oilAmt);
		 realVo.setAddOilSrvAmt(addOilSrvAmt);
		 
	}

	private void putPlatformDeductAmt(OrderOwnerCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		 String platformDeductionAmt = "0";
		 
//		 String ownerPayToPlatform = "0";
		 
		 //已经外置了
		 String platformSrvFeeAmt = "0";
		 String platformAddOilSrvAmt = "0";
		 String gpsAmt = "0";
		 
		 String gpsDeposit = "0";
		 String carServiceSrvFee = "0"; 
		
		 int srvFee = 0;
		 int oil = 0;  //平台加油服务费。
		 int gps = 0;
		 
		 int ownerPay = 0;  //默认
		 int gpsDepositAmt = 0;  //默认
		 int getReturnCarFee = 0;  //配送,增值订单
		 
		 /*代管车服务费*/
//		 OwnerOrderPurchaseDetailEntity proxyExpense = data.getProxyExpense();
//		 if(proxyExpense != null) {
//			 srvFee = proxyExpense.getTotalAmount();
//		 }
//	    /**
//	     * 车主端平台服务费
//	     */
//	     OwnerOrderPurchaseDetailEntity serviceExpense = data.getServiceExpense();
//	     if(serviceExpense != null) {
//	    	 srvFee += serviceExpense.getTotalAmount();
//	     }
	     
	     /**
	      * 加油服务费
	      */
//	     OwnerOrderPurchaseDetailEntity ownerOrderCostDetail = data.getOwnerOrderCostDetail();
//	     if(ownerOrderCostDetail != null) {
//	    	 oil += ownerOrderCostDetail.getTotalAmount().intValue();
//	     }
	     //直接取值。 200215修改。
//	     oil = data.getOwnerOilDifferenceCrashAmt();
	     
	     /**
	      * 获取gps服务费
	      */
//	      List<OwnerOrderPurchaseDetailEntity> gpsCost = data.getGpsCost();
//	      if(gpsCost != null) {
//	    	  for (OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity : gpsCost) {
//	    		  gps += ownerOrderPurchaseDetailEntity.getTotalAmount().intValue();
//	    	  }
//		  }
	      
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
	 
	       //新
	       // 计算 Gps 和平台服务费(直接取表中的记录。根据子订单号来查询。) 
	       //代码重构，是data中获取，而不是重复查询。200306
	       //之前海豹的代码在controller层重复查询。以重构到service层。
       	List<OwnerOrderIncrementDetailEntity> list = data.getOwnerOrderIncrementDetail(); // //ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(ownerCostReqVO.getOrderNo(),ownerCostReqVO.getOwnerOrderNo());
        if(!CollectionUtils.isEmpty(list)){
            gps = list.stream().filter(obj ->{
                return OwnerCashCodeEnum.GPS_SERVICE_AMT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
            int serviceAmt = list.stream().filter(obj ->{
                return OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo().equals(obj.getCostCode());
            }).mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
            int proxyServiceAmt = list.stream().filter(obj ->{
                return OwnerCashCodeEnum.PROXY_CHARGE.getCashNo().equals(obj.getCostCode());
            }).mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
            
            srvFee = serviceAmt + proxyServiceAmt;
        }

        //计算平台加油服务费：(仅仅车主端有)
//        统一从预结算中获取。
//         int ownerPlatFormOilService = deliveryCarInfoPriceService.getOwnerPlatFormOilServiceChargeByOrderNo(ownerCostReqVO.getOrderNo());
         oil = data.getOwnerPlatFormOilService();
		 
		 int total = ownerPay + gpsDepositAmt + getReturnCarFee + gps + oil + srvFee; //3项外置，也需要累加。
		 
		 platformDeductionAmt = String.valueOf( NumberUtils.convertNumberToFushu(total));
		 
		 platformSrvFeeAmt = String.valueOf( NumberUtils.convertNumberToFushu(srvFee));
		 platformAddOilSrvAmt = String.valueOf(NumberUtils.convertNumberToFushu(oil));
		 gpsAmt = String.valueOf(NumberUtils.convertNumberToFushu(gps));
		 
		 //被覆盖了。。。
//		 ownerPayToPlatform = String.valueOf(NumberUtils.convertNumberToFushu(ownerPay));
		 gpsDeposit = String.valueOf(gpsDepositAmt);
		 carServiceSrvFee = String.valueOf(NumberUtils.convertNumberToFushu(getReturnCarFee));
		 
		 //封装
		 realVo.setPlatformDeductionAmt(platformDeductionAmt);
		 realVo.setPlatformSrvFeeAmt(platformSrvFeeAmt);
		 realVo.setPlatformAddOilSrvAmt(platformAddOilSrvAmt);
		 realVo.setGpsAmt(gpsAmt);
		 
//		 realVo.setOwnerPayToPlatform(ownerPayToPlatform);
		 realVo.setGpsDeposit(gpsDeposit);  //默认处理
		 realVo.setCarServiceSrvFee(carServiceSrvFee);
		 
	}
	
//	private DeliveryCarVO getDeliveryCarVO(String orderNo){
//        DeliveryCarRepVO deliveryCarDTO = new DeliveryCarRepVO();
//        deliveryCarDTO.setOrderNo(orderNo);
//        DeliveryCarVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
//        return deliveryCarRepVO;
//    }

	private void putPlatformSubsidyAndOwnerCoupon(OrderOwnerCostResVO realVo,
			com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
		 String couponDeductionAmount = "0";
		 String ownerCouponTitle = ""; 
		 String ownerCouponAmt = "0";
		 String ownerSubsidyRentAmt = "0";
		 //补贴
		 String platformSubsidyTotalAmt = "0";
		 String platformSubsidyAmt = "0";
		 ///
		 String ownerToRenterAdjustAmt = "0";
		 
		 int platformSubsidyAmount = 0;
		 int ownerSubsidyRentAmount = 0;
		 
		 int ownerToRenterAdjustAmount = 0;
		 /**
		  * 管理后台补贴
		  */
		 List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = data.getOrderConsoleSubsidyDetails();
			for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : orderConsoleSubsidyDetails) {
				//`subsidy_source_name` varchar(16) DEFAULT NULL COMMENT '补贴来源方 1、租客 2、车主 3、平台',
				//  `subsidy_target_name` varchar(16) DEFAULT NULL COMMENT '补贴方名称 1、租客 2、车主 3、平台',
				if("3".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "2".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())) {
					platformSubsidyAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}	
			}
			
			/**
			 * 车主补贴 20200205
			 */
			List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = data.getOwnerOrderSubsidyDetail();
			for (OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity : ownerOrderSubsidyDetail) {
				if("2".equals(ownerOrderSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(ownerOrderSubsidyDetailEntity.getSubsidyTargetCode())) {
					//租金的补贴
					if(ownerOrderSubsidyDetailEntity.getSubsidyCostCode().equals(RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo())) {
						ownerSubsidyRentAmount += ownerOrderSubsidyDetailEntity.getSubsidyAmount().intValue();
					}
				}
			}
			

			//调价  车主的会员号相同。 需要求和。
			for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : orderConsoleSubsidyDetails) {
			  //车主给租客的调价
				if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
					if(RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
						ownerToRenterAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
					}
				}else if("1".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "2".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
					if(RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
						//不需要累计，只是查询记录
						ownerToRenterAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
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
			
			
			platformSubsidyTotalAmt = String.valueOf( NumberUtils.convertNumberToZhengshu(platformSubsidyAmount));	
			platformSubsidyAmt = String.valueOf( NumberUtils.convertNumberToZhengshu(platformSubsidyAmount));	
			
			ownerCouponAmt = String.valueOf( NumberUtils.convertNumberToFushu(ownerCouponAmtInt));
			couponDeductionAmount = String.valueOf( NumberUtils.convertNumberToFushu(ownerCouponAmtInt + ownerSubsidyRentAmount));
			ownerSubsidyRentAmt = String.valueOf( NumberUtils.convertNumberToFushu(ownerSubsidyRentAmount));
			
			//存在正负号区别。
			ownerToRenterAdjustAmt = String.valueOf(ownerToRenterAdjustAmount);  //NumberUtils.convertNumberToZhengshu(ownerToRenterAdjustAmount)
			
		 //抵扣  给租客的优惠
		 realVo.setCouponDeductionAmount(couponDeductionAmount);
		 realVo.setOwnerCouponTitle(ownerCouponTitle);
		 realVo.setOwnerCouponAmt(ownerCouponAmt);
		 realVo.setOwnerSubsidyRentAmt(ownerSubsidyRentAmt);
		 /////平台补贴
		 realVo.setPlatformSubsidyAmt(platformSubsidyAmt);
		 realVo.setPlatformSubsidyTotalAmt(platformSubsidyTotalAmt);
		 ///车主给租客的调价
		 realVo.setAdjustAmt(ownerToRenterAdjustAmt);
	}

    public OrderRenterCostResVO calculateRenterOrderCostLongRent(RenterCostReqVO renterCostReqVO) throws Exception {
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
//		realVo.setAdjustAmt("0");
        //加油服务费 huangjing-todo
        realVo.setAddOilSrvAmt("0");
        ///租客需支付给平台的费用 huangjing-todo  -DO
//		realVo.setRenterPayToPlatform("0");

        ResponseData<com.atzuche.order.commons.vo.res.OrderRenterCostResVO> resData = feignOrderCostService.orderCostRenterGet(req);
        if(resData != null) {
            com.atzuche.order.commons.vo.res.OrderRenterCostResVO data = resData.getData();
            if(data != null) {
                int renterCostAmtFinal = data.getRenterCostAmtFinal();
                RenterCostVO costVo = orderSettleService.getRenterCostByOrderNo(renterCostReqVO.getOrderNo(),renterCostReqVO.getRenterOrderNo(),orderEntity.getMemNoRenter(),renterCostAmtFinal);

                //租金费用  费用明细表renter_order_cost_detail
                putRenterOrderCostDetail(realVo,data);

                //租客订单对象
                RenterOrderEntity entity = renterOrderService.getRenterOrderByRenterOrderNo(renterCostReqVO.getRenterOrderNo());
                //优惠抵扣  优惠抵扣 券，凹凸币，钱包   钱包抵扣
                putRenterOrderDeduct(realVo,data,entity,orderEntity.getMemNoRenter());
                //长租折扣 TODO
                longRentDeduct(realVo,data);

                //租车押金和违章押金   车辆押金  违章押金
                putRenterOrderDeposit(realVo,data,costVo);

                //取还车服务违约金   renter_order_fine_deatail  取还车服务违约金
                putRenterOrderFine(realVo,data);

                //租车费用应收@海豹   实收
                putPaymentAmount(realVo,data,costVo);

                //油费,超里程
                putOilBeyondMile(realVo,data);

                //平台给租客的补贴， 车主和租客的调价，车主给租客的租金补贴
                putConsoleSubsidy(realVo,data);

                //补付费用
                //需补付金额,跟修改订单的补付金额是同一个方法。
                putSupplementAmt(realVo,data,costVo);

                //租客支付给平台的费用。console  200214
                putRenterToPlatformCost(realVo,data);

                //rentFeeBase基础费用
                putRentFeeBase(realVo,data);
            }
        }

        return realVo;
    }

    public OrderOwnerCostResVO calculateOwnerOrderCostLong(OwnerCostReqVO ownerCostReqVO) throws Exception {
        OrderOwnerCostResVO realVo = new OrderOwnerCostResVO();

        OwnerOrderEntity orderEntityOwner = null;
        if(StringUtils.isNotBlank(ownerCostReqVO.getOwnerOrderNo())) {
            orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
            if(orderEntityOwner == null){
                logger.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
                throw new Exception("获取订单数据(车主)为空");

            }
        }
        OrderCostReqVO req = new OrderCostReqVO();
        req.setOrderNo(ownerCostReqVO.getOrderNo());
        req.setSubOrderNo(ownerCostReqVO.getOwnerOrderNo());
        req.setMemNo(orderEntityOwner.getMemNo());
        ResponseData<com.atzuche.order.commons.vo.res.OrderOwnerCostResVO> resData = feignOrderCostService.orderCostOwnerGet(req);

        //子订单号
        realVo.setOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
        //默认值处理  调价目前没有
//		realVo.setAdjustAmt("0");
        //加油服务费
        realVo.setAddOilSrvAmt("0");
        ///车主需支付给平台的费用
//		realVo.setOwnerPayToPlatform("0");


        if(resData != null) {
            com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data = resData.getData();
            if(data != null) {
                //租客支付给平台的费用。console  200214
                putOwnerToPlatformCost(realVo,data);

                //给租客的优惠（车主券）和平台补贴   调价
                putPlatformSubsidyAndOwnerCoupon(realVo,data);
                //长租折扣
                putPlatformSubsidyAndOwnerCouponLong(realVo,data);

                //扣款
                putPlatformDeductAmt(realVo,data);

                //收益
                putBaseFee(realVo,data,ownerCostReqVO);

                //最后算
                putIncome(realVo,data);

            }
        }else {
            logger.error("feign接口返回resData null!!! params={}",ownerCostReqVO.toString());
        }

        return realVo;
    }

    private void putPlatformSubsidyAndOwnerCouponLong(OrderOwnerCostResVO realVo, com.atzuche.order.commons.vo.res.OrderOwnerCostResVO data) {
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = data.getOwnerOrderSubsidyDetail();
        int sum = Optional.ofNullable(ownerOrderSubsidyDetail)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> RenterCashCodeEnum.RENT_AMT.getCashNo().equals(x.getSubsidyCostCode()))
                .mapToInt(OwnerOrderSubsidyDetailEntity::getSubsidyAmount)
                .sum();

        OwnerCouponLongDTO ownerCouponLongDTO = data.getOwnerCouponLongDTO();
        if(ownerCouponLongDTO != null){
            realVo.setOwnerLongRentDeduct(ownerCouponLongDTO.getDiscountDesc());
        }
        realVo.setOwnerLongRentDeductAmt(String.valueOf(NumberUtils.convertNumberToFushu(sum)));
    }
}
