/**
 * 
 */
package com.atzuche.order.admin.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.enums.YesNoEnum;
import com.atzuche.order.admin.vo.req.cost.GetReturnRequestVO;
import com.atzuche.order.admin.vo.resp.cost.GetReturnCostVO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.rentercost.entity.dto.GetReturnCostDTO;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverCostDTO;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverTransportDTO;
import com.atzuche.order.rentercost.entity.vo.GetReturnResponseVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;

/**
 * @author jing.huang
 *
 */
@Service
public class GetReturnCarService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	
	
	public GetReturnCostVO calculateGetOrReturnCost(GetReturnRequestVO vo,LocalDateTime startTime,LocalDateTime endTime) {
		GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
		GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
		
		GetReturnCostVO respVo = new GetReturnCostVO();
		/**
		 * 封装参数
		 */
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		
		costBaseDTO.setStartTime(startTime);
		costBaseDTO.setEndTime(endTime);
		
		String flag = vo.getFlag();
		if("1".equals(flag)) {  //取车
			getReturnCarCostReqDto.setIsGetCarCost(true);
			getReturnCarCostReqDto.setIsReturnCarCost(false);
		} else { //还车
			getReturnCarCostReqDto.setIsGetCarCost(false);
			getReturnCarCostReqDto.setIsReturnCarCost(true);
		}
		getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
		getReturnCarCostReqDto.setSrvGetLat(vo.getGetCarLan());
		getReturnCarCostReqDto.setSrvGetLon(vo.getGetCarLon());
		getReturnCarCostReqDto.setSrvReturnLat(vo.getReturnCarLan());
		getReturnCarCostReqDto.setSrvReturnLon(vo.getReturnCarLon());
		getReturnCarCostReqDto.setEntryCode(vo.getSceneCode());
		getReturnCarCostReqDto.setSource(Integer.valueOf(vo.getSource()));
		getReturnCarCostReqDto.setCityCode(Integer.valueOf(vo.getCityCode()));
		if("2".equals(vo.getOrderType())) {
			getReturnCarCostReqDto.setIsPackageOrder(true);
		}else {
			getReturnCarCostReqDto.setIsPackageOrder(false);
		}
		
		///
		getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
		getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(vo.getCityCode()));
		getReturnCarOverCostReqDto.setOrderType(Integer.valueOf(vo.getOrderType()));
		/**
		 * 计算取还车费用
		 */
		GetReturnCostDTO dto = renterOrderCostCombineService.getReturnCarCost(getReturnCarCostReqDto);
		
		/**
		 * 计算高峰运能
		 */
		GetReturnOverCostDTO overDto = renterOrderCostCombineService.getGetReturnOverCost(getReturnCarOverCostReqDto);
		
		/**
		 * 数据封装
		 */
		if(dto != null) {
			GetReturnResponseVO grrv = dto.getGetReturnResponseVO();
			
			if(grrv != null) {
				logger.info("GetReturnResponseVO data={}", grrv.toString());
				respVo.setGetCost(String.valueOf(grrv.getGetFee()));
				respVo.setReturnCost(String.valueOf(grrv.getReturnFee()));
				respVo.setGetShouldCost(String.valueOf(grrv.getGetShouldFee()));
				respVo.setReturnShouldCost(String.valueOf(grrv.getReturnShouldFee()));
			}
		}
		if(overDto != null) {
			GetReturnOverTransportDTO grot = overDto.getGetReturnOverTransportDTO();
			if(grot != null) {
				logger.info("GetReturnOverTransportDTO data={}", grot.toString());
				if(grot.getIsGetOverTransport()) {
					respVo.setIsGetOverTransport(YesNoEnum.YES.getType());
				}else {
					respVo.setIsGetOverTransport(YesNoEnum.NO.getType());
				}
				respVo.setGetOverTransportFee(String.valueOf(grot.getGetOverTransportFee()));
				respVo.setReturnOverTransportFee(String.valueOf(grot.getReturnOverTransportFee()));
				respVo.setNightGetOverTransportFee(String.valueOf(grot.getNightGetOverTransportFee()));
				respVo.setNightReturnOverTransportFee(String.valueOf(grot.getNightReturnOverTransportFee()));
			}
		}
		
		return respVo;
	}
	
}
