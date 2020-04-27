package com.atzuche.order.coreapi.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.LongRentSubsidyService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LongRentCostHandleService {
	
	@Autowired
	private LongRentSubsidyService longRentSubsidyService;

	
	/**
	 * 处理长租费用逻辑
	 * @param renterOrderCostReqDTO
	 * @param renterOrderCostRespDTO
	 * @return RenterOrderCostRespDTO
	 */
	public RenterOrderCostRespDTO handRentCost(RenterOrderCostReqDTO renterOrderCostReqDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
		if (renterOrderSubsidyDetailDTOList == null) {
			renterOrderSubsidyDetailDTOList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		}
		// 获取取车费用补贴
		RenterOrderSubsidyDetailDTO longGetAmtSubsidy = longRentSubsidyService.getLongGetAmtSubsidy(renterOrderCostReqDTO, renterOrderCostRespDTO);
		renterOrderCostRespDTO.setGetRealAmt(0);
		if (longGetAmtSubsidy != null) {
			renterOrderSubsidyDetailDTOList.add(longGetAmtSubsidy);
		}
		// 获取还车费用补贴
		RenterOrderSubsidyDetailDTO longReturnAmtSubsidy = longRentSubsidyService.getLongReturnAmtSubsidy(renterOrderCostReqDTO, renterOrderCostRespDTO);
		renterOrderCostRespDTO.setReturnRealAmt(0);
		if (longReturnAmtSubsidy != null) {
			renterOrderSubsidyDetailDTOList.add(longReturnAmtSubsidy);
		}
		// 获取取车运能补贴
		RenterOrderSubsidyDetailDTO longGetOverAmtSubsidy = longRentSubsidyService.getLongGetOverAmtSubsidy(renterOrderCostReqDTO, renterOrderCostRespDTO);
		renterOrderCostRespDTO.setGetOverAmt(0);
		if (longGetOverAmtSubsidy != null) {
			renterOrderSubsidyDetailDTOList.add(longGetOverAmtSubsidy);
		}
		// 获取还车运能补贴
		RenterOrderSubsidyDetailDTO longReturnOverAmtSubsidy = longRentSubsidyService.getLongReturnOverAmtSubsidy(renterOrderCostReqDTO, renterOrderCostRespDTO);
		renterOrderCostRespDTO.setReturnOverAmt(0);
		if (longReturnOverAmtSubsidy != null) {
			renterOrderSubsidyDetailDTOList.add(longReturnOverAmtSubsidy);
		}
		renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(renterOrderSubsidyDetailDTOList);
		// 不计算保险和不计免赔
		renterOrderCostRespDTO.setBasicEnsureAmount(0);
		renterOrderCostRespDTO.setComprehensiveEnsureAmount(0);
		List<RenterOrderCostDetailEntity> renterOrderCostDetailDTOList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
		if (renterOrderCostDetailDTOList == null || renterOrderCostDetailDTOList.isEmpty()) {
			return renterOrderCostRespDTO;
		}
		Iterator<RenterOrderCostDetailEntity> it = renterOrderCostDetailDTOList.iterator();
		while(it.hasNext()){
			RenterOrderCostDetailEntity entity = it.next();  
	        if(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(entity.getCostCode()) || 
	        		RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(entity.getCostCode())){  
	        	it.remove();  
	        }  
	     }
		return renterOrderCostRespDTO;
	}
}
