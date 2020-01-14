/**
 * 
 */
package com.atzuche.order.admin.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.vo.req.order.ModificationOrderRequestVO;
import com.atzuche.order.admin.vo.resp.order.ModificationOrderListResponseVO;
import com.atzuche.order.admin.vo.resp.order.ModificationOrderResponseVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.ModifyOrderMainQueryReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderQueryReqVO;
import com.atzuche.order.commons.vo.res.ModifyOrderMainResVO;
import com.atzuche.order.commons.vo.res.ModifyOrderResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.order.RenterOrderResVO;
import com.atzuche.order.open.service.FeignOrderModifyService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.commons.utils.StringUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.platformcost.CommonUtils;

/**
 * @author jing.huang
 *
 */
@Service
public class ModificationOrderService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	FeignOrderModifyService feignOrderModifyService;
	@Autowired
	OrderService orderService;
	
	public ModificationOrderListResponseVO queryModifyList(ModificationOrderRequestVO modificationOrderRequestVO) throws Exception{
		ModificationOrderListResponseVO respVo = new ModificationOrderListResponseVO();
		List<ModificationOrderResponseVO> modificationOrderList =  new ArrayList<ModificationOrderResponseVO>();
		//主订单
        OrderEntity orderEntity = orderService.getOrderEntity(modificationOrderRequestVO.getOrderNo());
        if(orderEntity == null){
        	logger.error("获取订单数据为空orderNo={}",modificationOrderRequestVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }
        
		ModifyOrderMainQueryReqVO req = new ModifyOrderMainQueryReqVO();
//		BeanUtils.copyProperties(req, modificationOrderRequestVO);
		req.setOrderNo(modificationOrderRequestVO.getOrderNo());
		req.setMemNo(orderEntity.getMemNoRenter());
        
		ResponseData<ModifyOrderMainResVO> resData = feignOrderModifyService.getModifyOrderMain(req);
		if(resData != null) {
			ModifyOrderMainResVO data = resData.getData();
			
			if(data != null) {
				//查询子订单表
				List<RenterOrderResVO> renterOrderLst =  data.getRenterOrderLst();
				/**
				 * 租客订单列表
				 */
				for (RenterOrderResVO renterOrderResVO : renterOrderLst) {
					ModificationOrderResponseVO realVo = new ModificationOrderResponseVO();
					//这里应该是根据主订单和子订单号查询。
					ModifyOrderQueryReqVO req2 = new ModifyOrderQueryReqVO();
					req2.setOrderNo(renterOrderResVO.getOrderNo());
					req2.setRenterOrderNo(renterOrderResVO.getRenterOrderNo());
					/**
					 * 子订单查询
					 */
					ResponseData<ModifyOrderResVO> resSubData = feignOrderModifyService.queryModifyOrderList(req2);
					if(resSubData != null) {
						ModifyOrderResVO subData = resSubData.getData();
						if(subData != null) {
							//租客子订单基本数据
							putRenterOrderDetail(realVo,data,subData,renterOrderResVO);
							
							//租金费用  费用明细表renter_order_cost_detail   
							putRenterOrderCostDetail(realVo,data,subData);
							
							//取还车 renter_order_delivery 查询配送信息表
							putRenterOrderGetReturn(realVo,data,subData);
							
							//优惠抵扣  优惠抵扣 券，凹凸币，钱包   钱包抵扣
							putRenterOrderDeduct(realVo,data,subData);
							
							//租车押金和违章押金   车辆押金  违章押金
							putRenterOrderDeposit(realVo,data,subData);
							
							//取还车服务违约金   renter_order_fine_deatail  取还车服务违约金
							putRenterOrderFine(realVo,data,subData);
							
							//需补付金额   需补付金额
							putPaymentAmount(realVo,data,subData);
						}
					}
					//封装数据 
					modificationOrderList.add(realVo);
				}
			}
		}
		
		//封装数据
		respVo.setModificationOrderList(modificationOrderList);  //否则返回空，不报NPE
		return respVo;
	}

	private void putRenterOrderFine(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData) {
		int carServiceFine = 0;
		List<RenterOrderFineDeatailResVO> fineLst = subData.getFineLst();
		for (RenterOrderFineDeatailResVO renterOrderFineDeatailResVO : fineLst) {
			//fine_type  罚金类型：1-修改订单取车违约金，2-修改订单还车违约金
			if(renterOrderFineDeatailResVO.getFineType().intValue() == 1) {
				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
			}
			if(renterOrderFineDeatailResVO.getFineType().intValue() == 2) {
				carServiceFine += renterOrderFineDeatailResVO.getFineAmount().intValue();
			}
		}
		
		realVo.setCarServiceFine(String.valueOf(carServiceFine));
	}

	private void putRenterOrderDeposit(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData) {
		realVo.setVehicleDeposit(data.getRentVo()!=null?String.valueOf(data.getRentVo().getYingfuDepositAmt()):"---");
		realVo.setViolationDeposit(data.getWzVo()!=null?String.valueOf(data.getWzVo().getYingshouDeposit()):"---");
	}

	private void putRenterOrderDeduct(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData) {
		int walletAmt = data.getWalletCostDetail().getAmt();
		List<RenterOrderSubsidyDetailResVO> subsidyLst = subData.getSubsidyLst();
		//优惠券抵扣金额
		String plateformCouponCashNo =  RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo();
		//车主券抵扣金额
		String ownerCouponCashNo =  RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo();
		//凹凸币抵扣金额
		String aotuCoinCashNo =  RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo();
		//默认0
		int plateformCoupon = 0;
		int ownerCoupon = 0;
		int aotuCoin = 0;
				
		for (RenterOrderSubsidyDetailResVO renterOrderSubsidyDetailResVO : subsidyLst) {
			//12120010  12120051  12120012
			if(plateformCouponCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				plateformCoupon +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}else if(ownerCouponCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				ownerCoupon +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}else if(aotuCoinCashNo.equals(renterOrderSubsidyDetailResVO.getSubsidyCostCode())) {
				aotuCoin +=  renterOrderSubsidyDetailResVO.getSubsidyAmount().intValue();
			}
		}
		int deductionAmount = plateformCoupon + ownerCoupon + aotuCoin + walletAmt;
		realVo.setDeductionAmount(String.valueOf(deductionAmount));
	}

	private void putRenterOrderGetReturn(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData) {
		realVo.setGetAddress(subData.getRenterOrderDeliveryGet()!=null?subData.getRenterOrderDeliveryGet().getRenterGetReturnAddr():"---");
		realVo.setReturnAddress(subData.getRenterOrderDeliveryReturn()!=null?subData.getRenterOrderDeliveryReturn().getRenterGetReturnAddr():"---");
	}

	private void putRenterOrderCostDetail(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData) {
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
		List<RenterOrderCostDetailResVO> costList = subData.getRenterOrderCostDetailList();
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
		realVo.setRentAmount(String.valueOf(rentAmount));
		realVo.setInsuranceAmount(String.valueOf(insuranceAmount));
		realVo.setSupperInsuranceAmount(String.valueOf(supperInsuranceAmount));
		realVo.setAdditionalDriverInsuranceAmount(String.valueOf(additionalDriverInsuranceAmount));
		realVo.setServiceCharge(String.valueOf(serviceCharge));
		realVo.setCarServiceFee(String.valueOf(carServiceFee));
		
	}

	private void putRenterOrderDetail(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData,RenterOrderResVO renterOrderResVO) {
		realVo.setRenterOrderNo(renterOrderResVO.getRenterOrderNo());
		realVo.setModificationTime(LocalDateTimeUtils.formatDateTime(renterOrderResVO.getCreateTime()));
		realVo.setSource(convertSource(renterOrderResVO.getChangeSource()));
		realVo.setModificationUser(renterOrderResVO.getCreateOp());
		realVo.setModificationReason("---");
		realVo.setRentTime(LocalDateTimeUtils.formatDateTime(renterOrderResVO.getExpRentTime()));
		realVo.setRevertTime(LocalDateTimeUtils.formatDateTime(renterOrderResVO.getExpRevertTime()));
		realVo.setTotalRentDay(calcTotalRentDay(renterOrderResVO.getExpRentTime(),renterOrderResVO.getExpRevertTime()));
		realVo.setCarServiceInformation(convertCarGetReturn(renterOrderResVO));
		realVo.setOperatorStatus(convertAgreeFlag(renterOrderResVO));
	}
	
	private String convertAgreeFlag(RenterOrderResVO renterOrderResVO) {
		String flag = "";
		if(renterOrderResVO.getAgreeFlag() == null) {
			return "---";
		}
		switch (renterOrderResVO.getAgreeFlag()) {
		case 0:
			flag = "未处理";
			break;
		case 1:
			flag = "已同意";		
			break;
		case 2:
			flag = "已拒绝";
			break;
		default:
			break;
		}
		return flag;
	}

	private String convertCarGetReturn(RenterOrderResVO renterOrderResVO) {
		String carServiceInformation = "";  
		if(renterOrderResVO.getIsGetCar().intValue() == 1) {
			carServiceInformation = "取车";
		}
		if(renterOrderResVO.getIsReturnCar().intValue() == 1) {
			carServiceInformation += " 还车";
		}
		if(renterOrderResVO.getIsGetCar().intValue() == 0 && renterOrderResVO.getIsReturnCar().intValue() == 0) {
			carServiceInformation = "无";  
		}
		return carServiceInformation;
	}

	private String calcTotalRentDay(LocalDateTime expRentTime, LocalDateTime expRevertTime) {
		String totalRentDay = "---";
		//todo
		totalRentDay = String.valueOf(CommonUtils.getRentDays(expRentTime, expRevertTime, null))+"天";
		return totalRentDay;
	}

	private String convertSource(String changeSource) {
		String source = "";
		if(StringUtils.isBlank(changeSource)) {
			return "---";
		}
		switch (changeSource) {
		case "1":
			source = "后台管理";
			break;
		case "2":
			source = "租客";
			break;
		case "3":
			source = "车主";
			break;
		default:
			break;
		}
		return source;
	}

	private void putPaymentAmount(ModificationOrderResponseVO realVo, ModifyOrderMainResVO data,
			ModifyOrderResVO subData) {
		realVo.setPaymentAmount(String.valueOf(data.getNeedIncrementAmt()));
	}
	
}
