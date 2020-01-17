package com.atzuche.order.coreapi.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.entity.vo.CostDeductVO;
import com.atzuche.order.coreapi.entity.vo.ModifyOrderCompareVO;
import com.atzuche.order.coreapi.entity.vo.ModifyOrderCostVO;
import com.atzuche.order.coreapi.entity.vo.ModifyOrderDeductVO;
import com.atzuche.order.coreapi.entity.vo.ModifyOrderFeeVO;
import com.atzuche.order.coreapi.entity.vo.ModifyOrderFineVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderFeeService {
	
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private ModifyOrderService modifyOrderService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderStatusService orderStatusService;

	/**
	 * 获取修改订单前后费用
	 * @param modifyOrderReq
	 * @return ResponseData<ModifyOrderCompareVO>
	 */
	public ResponseData<ModifyOrderCompareVO> getModifyOrderCompareVO(ModifyOrderReq modifyOrderReq) {
		log.info("ModifyOrderFeeService.getModifyOrderCompareVO modifyOrderReq=[{}]", modifyOrderReq);
		// 主订单号
		String orderNo = modifyOrderReq.getOrderNo();
		// 获取修改前有效租客子订单信息
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// DTO包装
		ModifyOrderDTO modifyOrderDTO = modifyOrderService.getModifyOrderDTO(modifyOrderReq, null, initRenterOrder, deliveryList);
		log.info("ModifyOrderFeeService.getModifyOrderCompareVO modifyOrderDTO=[{}]", modifyOrderDTO);
		// 获取租客会员信息
		RenterMemberDTO renterMemberDTO = modifyOrderService.getRenterMemberDTO(initRenterOrder.getRenterOrderNo(), null);
		// 设置租客会员信息
		modifyOrderDTO.setRenterMemberDTO(renterMemberDTO);
		// 获取租客商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderService.getRenterGoodsDetailDTO(modifyOrderDTO, initRenterOrder);
		// 设置商品信息
		modifyOrderDTO.setRenterGoodsDetailDTO(renterGoodsDetailDTO);
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderByOrderNoAndMemNo(orderNo, modifyOrderReq.getMemNo());
		// 设置主订单信息
		modifyOrderDTO.setOrderEntity(orderEntity);
		// 查询主订单状态
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
		// 设置订单状态
		modifyOrderDTO.setOrderStatusEntity(orderStatusEntity);
		// 设置城市编号
		modifyOrderDTO.setCityCode(orderEntity.getCityCode());
		log.info("ModifyOrderFeeService.getModifyOrderCompareVO again modifyOrderDTO=[{}]", modifyOrderDTO);
		// 获取修改前租客费用明细
		List<RenterOrderCostDetailEntity> initCostList = renterOrderCostDetailService.listRenterOrderCostDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 获取修改前补贴信息
		List<RenterOrderSubsidyDetailEntity> initSubsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 获取修改前罚金
		List<RenterOrderFineDeatailEntity> initFineList = renterOrderFineDeatailService.listRenterOrderFineDeatail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 提前延后时间计算
		CarRentTimeRangeResVO carRentTimeRangeResVO = modifyOrderService.getCarRentTimeRangeResVO(modifyOrderDTO);
		// 设置提前延后时间
		modifyOrderDTO.setCarRentTimeRangeResVO(carRentTimeRangeResVO);
		// 封装计算用对象
		RenterOrderReqVO renterOrderReqVO = modifyOrderService.convertToRenterOrderReqVO(modifyOrderDTO, renterMemberDTO, renterGoodsDetailDTO, orderEntity, carRentTimeRangeResVO);
		// 基础费用计算包含租金，手续费，基础保障费用，全面保障费用，附加驾驶人保障费用，取还车费用计算和超运能费用计算
		RenterOrderCostRespDTO renterOrderCostRespDTO = modifyOrderService.getRenterOrderCostRespDTO(modifyOrderDTO, renterOrderReqVO, initCostList, initSubsidyList);
		// 获取修改订单违约金
		List<RenterOrderFineDeatailEntity> renterFineList = modifyOrderService.getRenterFineList(modifyOrderDTO, initRenterOrder, deliveryList, renterOrderCostRespDTO);
		// 封装基础信息对象
		CostBaseDTO costBaseDTO = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		// 获取抵扣补贴(含车主券，限时立减，取还车券，平台券和凹凸币)
		CostDeductVO costDeductVO = modifyOrderService.getCostDeductVO(modifyOrderDTO, costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, orderEntity, initSubsidyList);
		// 聚合租客补贴
		renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(modifyOrderService.getPolymerizationSubsidy(renterOrderCostRespDTO, costDeductVO));
		ModifyOrderCompareVO modifyOrderCompareVO = new ModifyOrderCompareVO();
		// 修改前费用
		ModifyOrderFeeVO initModifyOrderFeeVO = getInitModifyOrderFeeVO(initCostList, initSubsidyList, initFineList);
		// 修改后费用
		ModifyOrderFeeVO updateModifyOrderFeeVO = getUpdateModifyOrderFeeVO(renterOrderCostRespDTO, renterFineList);
		modifyOrderCompareVO.setInitModifyOrderFeeVO(initModifyOrderFeeVO);
		modifyOrderCompareVO.setUpdateModifyOrderFeeVO(updateModifyOrderFeeVO);
		return ResponseData.success(modifyOrderCompareVO);
	}
	
	
	/**
	 * 获取修改后费用/抵扣/罚金
	 * @param renterOrderCostRespDTO
	 * @param fineList
	 * @return ModifyOrderFeeVO
	 */
	public ModifyOrderFeeVO getUpdateModifyOrderFeeVO(RenterOrderCostRespDTO renterOrderCostRespDTO, List<RenterOrderFineDeatailEntity> fineList) {
		// 费用对应的明细列表
	    List<RenterOrderCostDetailEntity> costList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
	    // 补贴对应的明细列表
	    List<RenterOrderSubsidyDetailDTO> subsidyDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
	    List<RenterOrderSubsidyDetailEntity> subsidyList = new ArrayList<RenterOrderSubsidyDetailEntity>();
	    if (subsidyDTOList != null && !subsidyDTOList.isEmpty()) {
	    	for (RenterOrderSubsidyDetailDTO subDTO:subsidyDTOList) {
	    		RenterOrderSubsidyDetailEntity subsidEntity = new RenterOrderSubsidyDetailEntity();
	    		BeanUtils.copyProperties(subDTO, subsidEntity);
	    		subsidyList.add(subsidEntity);
	    	}
	    }
	    // 获取费用对象
 		ModifyOrderCostVO modifyOrderCostVO = getModifyOrderCostVO(costList, subsidyList);
 		// 获取抵扣对象
 		ModifyOrderDeductVO modifyOrderDeductVO = getModifyOrderDeductVO(subsidyList);
 		// 获取罚金对象
 		ModifyOrderFineVO modifyOrderFineVO = getModifyOrderFineVO(fineList);
 		// 封装
 		ModifyOrderFeeVO updateModifyOrderFeeVO = new ModifyOrderFeeVO();
 		updateModifyOrderFeeVO.setModifyOrderCostVO(modifyOrderCostVO);
 		updateModifyOrderFeeVO.setModifyOrderDeductVO(modifyOrderDeductVO);
 		updateModifyOrderFeeVO.setModifyOrderFineVO(modifyOrderFineVO);
 		return updateModifyOrderFeeVO;
	}
	
	
	/**
	 * 获取修改前费用/抵扣/罚金
	 * @param modifyOrderReq
	 * @param initRenterOrder
	 * @return ModifyOrderFeeVO
	 */
	public ModifyOrderFeeVO getInitModifyOrderFeeVO(List<RenterOrderCostDetailEntity> costList, List<RenterOrderSubsidyDetailEntity> subsidyList, List<RenterOrderFineDeatailEntity> fineList) {
		// 获取费用对象
		ModifyOrderCostVO modifyOrderCostVO = getModifyOrderCostVO(costList, subsidyList);
		// 获取抵扣对象
		ModifyOrderDeductVO modifyOrderDeductVO = getModifyOrderDeductVO(subsidyList);
		// 获取罚金对象
		ModifyOrderFineVO modifyOrderFineVO = getModifyOrderFineVO(fineList);
		// 封装
		ModifyOrderFeeVO initModifyOrderFeeVO = new ModifyOrderFeeVO();
		initModifyOrderFeeVO.setModifyOrderCostVO(modifyOrderCostVO);
		initModifyOrderFeeVO.setModifyOrderDeductVO(modifyOrderDeductVO);
		initModifyOrderFeeVO.setModifyOrderFineVO(modifyOrderFineVO);
		return initModifyOrderFeeVO;
	}
	
	/**
	 * 获取费用对象
	 * @param costList
	 * @param subsidyList
	 * @return ModifyOrderCostVO
	 */
	public ModifyOrderCostVO getModifyOrderCostVO(List<RenterOrderCostDetailEntity> costList, List<RenterOrderSubsidyDetailEntity> subsidyList) {
		if (costList == null || costList.isEmpty()) {
			return null;
		}
		// 租金
		Integer rentAmt = getCostAmtByCode(costList, RenterCashCodeEnum.RENT_AMT.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.RENT_AMT.getCashNo());
		// 手续费
		Integer poundageAmt = getCostAmtByCode(costList, RenterCashCodeEnum.FEE.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.FEE.getCashNo());
		// 平台保障费
		Integer insuranceAmt = getCostAmtByCode(costList, RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo());
		// 全面保障费
		Integer abatementAmt = getCostAmtByCode(costList, RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo());
		// 附加驾驶人保险费
		Integer totalDriverFee = getCostAmtByCode(costList, RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo()); 
		// 取车费用
		Integer getCost = getCostAmtByCode(costList, RenterCashCodeEnum.SRV_GET_COST.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.SRV_GET_COST.getCashNo());
		// 还车费用
		Integer returnCost = getCostAmtByCode(costList, RenterCashCodeEnum.SRV_RETURN_COST.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
		// 取车运能加价
		Integer getBlockedRaiseAmt = getCostAmtByCode(costList, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo());
		// 还车运能加价
		Integer returnBlockedRaiseAmt = getCostAmtByCode(costList, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo()) + getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo());
		// 封装费用对象
		ModifyOrderCostVO modifyOrderCostVO = new ModifyOrderCostVO();
		modifyOrderCostVO.setAbatementAmt(abatementAmt);
		modifyOrderCostVO.setGetBlockedRaiseAmt(getBlockedRaiseAmt);
		modifyOrderCostVO.setGetCost(getCost);
		modifyOrderCostVO.setInsuranceAmt(insuranceAmt);
		modifyOrderCostVO.setPoundageAmt(poundageAmt);
		modifyOrderCostVO.setRentAmt(rentAmt);
		modifyOrderCostVO.setReturnBlockedRaiseAmt(returnBlockedRaiseAmt);
		modifyOrderCostVO.setReturnCost(returnCost);
		modifyOrderCostVO.setTotalDriverFee(totalDriverFee);
		return modifyOrderCostVO;
	}
	
	/**
	 * 获取抵扣对象
	 * @param subsidyList
	 * @return ModifyOrderDeductVO
	 */
	public ModifyOrderDeductVO getModifyOrderDeductVO(List<RenterOrderSubsidyDetailEntity> subsidyList) {
		if (subsidyList == null || subsidyList.isEmpty()) {
			return null;
		}
		// 车主券抵扣金额
		Integer ownerCouponOffsetCost = getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo());
		// 限时立减
		Integer reductionAmt = getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.REAL_LIMIT_REDUCTI.getCashNo());
		// 平台券抵扣金额
		Integer discouponAmt = getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo());
		// 取送服务券抵扣金额
		Integer getCarFeeDiscouponOffsetAmt = getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo());
		// 凹凸币抵扣金额
		Integer autoCoinDeductibleAmt = getSubsidyAmtByCode(subsidyList, RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo());
		// 封装抵扣对象
		ModifyOrderDeductVO modifyOrderDeductVO = new ModifyOrderDeductVO();
		modifyOrderDeductVO.setAutoCoinDeductibleAmt(autoCoinDeductibleAmt);
		modifyOrderDeductVO.setDiscouponAmt(discouponAmt);
		modifyOrderDeductVO.setGetCarFeeDiscouponOffsetAmt(getCarFeeDiscouponOffsetAmt);
		modifyOrderDeductVO.setOwnerCouponOffsetCost(ownerCouponOffsetCost);
		modifyOrderDeductVO.setReductionAmt(reductionAmt);
		return modifyOrderDeductVO;
	}
	
	
	/**
	 * 获取罚金对象
	 * @param fineList
	 * @return ModifyOrderFineVO
	 */
	public ModifyOrderFineVO getModifyOrderFineVO(List<RenterOrderFineDeatailEntity> fineList) {
		if (fineList == null || fineList.isEmpty()) {
			return null;
		}
		// 提前还车违约金
		Integer penaltyAmt = getFineAmtByCode(fineList, FineTypeEnum.MODIFY_ADVANCE.getFineType());
		// 取车服务违约金 
		Integer getFineAmt = getFineAmtByCode(fineList, FineTypeEnum.MODIFY_GET_FINE.getFineType());
		// 还车服务违约金
		Integer returnFineAmt = getFineAmtByCode(fineList, FineTypeEnum.MODIFY_RETURN_FINE.getFineType());
		// 封装罚金对象
		ModifyOrderFineVO modifyOrderFineVO = new ModifyOrderFineVO();
		modifyOrderFineVO.setGetFineAmt(getFineAmt);
		modifyOrderFineVO.setPenaltyAmt(penaltyAmt);
		modifyOrderFineVO.setReturnFineAmt(returnFineAmt);
		return modifyOrderFineVO;
	}
	
	
	/**
	 * 获取费用金额根据费用编码
	 * @param costList
	 * @param code
	 * @return Integer
	 */
	public Integer getCostAmtByCode(List<RenterOrderCostDetailEntity> costList, String code) {
		if (costList == null || costList.isEmpty()) {
			return 0;
		}
		if (StringUtils.isBlank(code)) {
			return 0;
		}
		return costList.stream().filter(cost -> {return code.equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
	}
	
	/**
	 * 获取补贴金额根据补贴费用编码
	 * @param subsidyList
	 * @param code
	 * @return Integer
	 */
	public Integer getSubsidyAmtByCode(List<RenterOrderSubsidyDetailEntity> subsidyList, String code) {
		if (subsidyList == null || subsidyList.isEmpty()) {
			return 0;
		}
		if (StringUtils.isBlank(code)) {
			return 0;
		}
		return subsidyList.stream().filter(sub -> {return code.equals(sub.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
	}
	
	/**
	 * 获取罚金根据罚金类型
	 * @param fineList
	 * @param fineType
	 * @return Integer
	 */
	public Integer getFineAmtByCode(List<RenterOrderFineDeatailEntity> fineList, Integer fineType) {
		if (fineList == null || fineList.isEmpty()) {
			return 0;
		}
		if (fineType == null) {
			return 0;
		}
		return fineList.stream().filter(fine -> {return fineType.equals(fine.getFineType());}).mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
	}
}
