package com.atzuche.order.renterorder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.autoyol.platformcost.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InsurAbamentDiscountService {
	
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;

	/**
	 * 获取平台保障费和全面保障费折扣补贴
	 * @param renterOrderCostReqDTO
	 * @param updSubsidyList
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getInsureDiscountSubsidy(RenterOrderCostReqDTO renterOrderCostReqDTO, List<RenterOrderSubsidyDetailDTO> updSubsidyList) {
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		if (renterOrderCostReqDTO == null) {
			return renterSubsidyList;
		}
		CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("获取平台保障费和全面保障费折扣补贴getInsureDiscountSubsidy costBaseDTO is null");
			return renterSubsidyList;
		}
		// 获取保险和不计免赔的折扣
		double insureDiscount = CommonUtils.getInsureDiscount(costBaseDTO.getStartTime(), costBaseDTO.getEndTime());
		log.info("获取平台保障费和全面保障费折扣补贴getInsureDiscountSubsidy orderNo=[{}],insureDiscount=[{}]",costBaseDTO.getOrderNo(), insureDiscount);
		if (insureDiscount >= 1.0) {
			return renterSubsidyList;
		}
		Map<String,RenterOrderSubsidyDetailDTO> subMap = updSubsidyList == null ? null:updSubsidyList.stream().collect(Collectors.toMap(RenterOrderSubsidyDetailDTO::getSubsidyCostCode, sub -> {return sub;}));
		if (subMap == null || subMap.get(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo()) == null) {
			//获取平台保障费
	        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
	        Integer insurAmt = insurAmtEntity.getTotalAmount();
	        // 单价
	        int unitPrice = (int) Math.ceil(insurAmtEntity.getUnitPrice()*insureDiscount);
	        // 折扣后的平台保障费
	        int afterDiscountInsurAmt = (int) Math.ceil(unitPrice*insurAmtEntity.getCount());
	        if (insurAmt != null && Math.abs(insurAmt) > Math.abs(afterDiscountInsurAmt)) {
	        	Integer subsidyAmount = Math.abs(insurAmt) - Math.abs(afterDiscountInsurAmt);
	        	// 产生补贴
	        	RenterOrderSubsidyDetailDTO subsidyDetail = convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.INSURE_AMT, 
	        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.INSURE_TOTAL_PRICES, "修改订单平台保障费打折补贴");
	        	renterSubsidyList.add(subsidyDetail);
	        }
		}
		if (subMap == null || subMap.get(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo()) == null) {
			//获取全面保障费
	        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
	        if (comprehensiveEnsureList != null && !comprehensiveEnsureList.isEmpty()) {
	        	List<RenterOrderSubsidyDetailDTO> abatementSubsidyList =
                        comprehensiveEnsureList.stream().map(fr -> getAbatementSubsidy(costBaseDTO, fr,
                                insureDiscount,"修改订单全面保障费打折补贴")).collect(Collectors.toList());
	        	renterSubsidyList.addAll(abatementSubsidyList);
	        }
		}
		return renterSubsidyList;
	}
	
	
	/**
	 * 获取全面保障费打折补贴
	 * @param costBaseDTO
	 * @param abatementSubsidyEntity
	 * @param insureDiscount
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getAbatementSubsidy(CostBaseDTO costBaseDTO,
                                                           RenterOrderCostDetailEntity abatementSubsidyEntity,
                                                           double insureDiscount, String subsidyDesc) {
		Integer insurAmt = abatementSubsidyEntity.getTotalAmount();
        // 单价
        int unitPrice = (int) Math.ceil(abatementSubsidyEntity.getUnitPrice()*insureDiscount);
        // 折扣后的平台保障费
        int afterDiscountInsurAmt = (int) Math.ceil(unitPrice*abatementSubsidyEntity.getCount());
        if (insurAmt != null && Math.abs(insurAmt) > Math.abs(afterDiscountInsurAmt)) {
        	Integer subsidyAmount = Math.abs(insurAmt) - Math.abs(afterDiscountInsurAmt);
        	// 产生补贴
        	return convertToRenterOrderSubsidyDetailDTO(costBaseDTO, subsidyAmount, SubsidyTypeCodeEnum.ABATEMENT_INSURE,
                    SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.ABATEMENT_INSURE, subsidyDesc);
        }
        log.info("获取全面保障费打折补贴.insurAmt:[{}],afterDiscountInsurAmt:[{}]", insurAmt, afterDiscountInsurAmt);
        return null;
	}
	
	/**
	 * 封装费用补贴对象
	 * @param costBaseDTO
	 * @param subsidyAmount
	 * @param type
	 * @param source
	 * @param targe
	 * @param cash
	 * @param subsidyDesc
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO convertToRenterOrderSubsidyDetailDTO(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidyTypeCodeEnum type, SubsidySourceCodeEnum source, SubsidySourceCodeEnum targe, RenterCashCodeEnum cash, String subsidyDesc) {
		RenterOrderSubsidyDetailDTO subd = new RenterOrderSubsidyDetailDTO();
		subd.setMemNo(costBaseDTO.getMemNo());
		subd.setOrderNo(costBaseDTO.getOrderNo());
		subd.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		subd.setSubsidyAmount(subsidyAmount);
		subd.setSubsidyCostCode(cash.getCashNo());
		subd.setSubsidyCostName(cash.getTxt());
		subd.setSubsidyDesc(subsidyDesc);
		subd.setSubsidySourceCode(source.getCode());
		subd.setSubsidySourceName(source.getDesc());
		subd.setSubsidyTargetCode(targe.getCode());
		subd.setSubsidyTargetName(targe.getDesc());
		subd.setSubsidyTypeCode(type.getCode());
		subd.setSubsidyTypeName(type.getDesc());
		return subd;
	}
}
