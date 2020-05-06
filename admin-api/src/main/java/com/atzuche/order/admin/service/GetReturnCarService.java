/**
 * 
 */
package com.atzuche.order.admin.service;

import com.atzuche.order.admin.enums.YesNoEnum;
import com.atzuche.order.admin.vo.req.cost.GetReturnRequestVO;
import com.atzuche.order.admin.vo.resp.cost.GetReturnCostVO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.enums.CategoryEnum;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeDetailVO;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeVO;
import com.atzuche.order.commons.vo.rentercost.GetReturnCostDTO;
import com.atzuche.order.commons.vo.rentercost.GetReturnOverCostDTO;
import com.atzuche.order.commons.vo.rentercost.GetReturnOverTransportDTO;
import com.atzuche.order.commons.vo.rentercost.GetReturnResponseVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author jing.huang
 *
 */
@Service
public class GetReturnCarService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String STR_ONE = "1";
	
	private static final String STR_ZERO = "0";

	@Autowired
	private OrderCostRemoteService orderCostRemoteService;
	
	/**
	 * 获取取送车和高峰运能费用
	 * @param vo
	 * @param startTime
	 * @param endTime
	 * @return GetReturnCostVO
	 */
	public GetReturnCostVO calculateGetOrReturnCost(GetReturnRequestVO vo,LocalDateTime startTime,LocalDateTime endTime) {
		
		GetReturnAndOverFeeDetailVO getReturnAndOverFeeDetailVO = getGetReturnAndOverFeeDetailVO(vo, startTime, endTime);
		if (getReturnAndOverFeeDetailVO == null) {
			return null;
		}
		// 取还车费
		GetReturnCostDTO dto = getReturnAndOverFeeDetailVO.getGetReturnCostDTO();
		// 高峰运能
		GetReturnOverCostDTO overDto = getReturnAndOverFeeDetailVO.getGetReturnOverCostDTO();
		// 数据封装
		GetReturnCostVO respVo = new GetReturnCostVO();
		respVo = getGetReturnCostVO(dto, vo.getOrderType(), respVo);
		respVo = getGetReturnOverCostVO(overDto, vo.getOrderType(), respVo);
		return respVo;
	}
	
	/**
	 * 数据封装
	 * @param vo
	 * @param startTime
	 * @param endTime
	 * @return GetReturnAndOverFeeDetailVO
	 */
	private GetReturnAndOverFeeDetailVO getGetReturnAndOverFeeDetailVO(GetReturnRequestVO vo,LocalDateTime startTime,LocalDateTime endTime) {
		GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
		GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
		// 封装参数
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		costBaseDTO.setStartTime(startTime);
		costBaseDTO.setEndTime(endTime);
		if(STR_ONE.equals(vo.getFlag())) {  //取车
			getReturnCarCostReqDto.setIsGetCarCost(true);
			getReturnCarCostReqDto.setIsReturnCarCost(false);
			getReturnCarOverCostReqDto.setIsGetCarCost(true);
			getReturnCarOverCostReqDto.setIsReturnCarCost(false);
		} else { //还车
			getReturnCarCostReqDto.setIsGetCarCost(false);
			getReturnCarCostReqDto.setIsReturnCarCost(true);
			getReturnCarOverCostReqDto.setIsGetCarCost(false);
			getReturnCarOverCostReqDto.setIsReturnCarCost(true);
		}
		getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
		getReturnCarCostReqDto.setSrvGetLat(vo.getGetCarLan());
		getReturnCarCostReqDto.setSrvGetLon(vo.getGetCarLon());
		getReturnCarCostReqDto.setSrvReturnLat(vo.getReturnCarLan());
		getReturnCarCostReqDto.setSrvReturnLon(vo.getReturnCarLon());
		getReturnCarCostReqDto.setEntryCode(vo.getSceneCode());
		getReturnCarCostReqDto.setSource(Integer.valueOf(vo.getSource()));
		getReturnCarCostReqDto.setCityCode(Integer.valueOf(vo.getCityCode()));
		if(CategoryEnum.SET_MEAL.getCode().equals(vo.getOrderType())) {
			getReturnCarCostReqDto.setIsPackageOrder(true);
		}else {
			getReturnCarCostReqDto.setIsPackageOrder(false);
		}
		getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
		getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(vo.getCityCode()));
		getReturnCarOverCostReqDto.setOrderType(Integer.valueOf(vo.getOrderType()));
		// 调远程获取取还车和高峰运能费
		GetReturnAndOverFeeVO getReturnAndOverFeeVO = new GetReturnAndOverFeeVO();
		getReturnAndOverFeeVO.setGetReturnCarCostReqDto(getReturnCarCostReqDto);
		getReturnAndOverFeeVO.setGetReturnCarOverCostReqDto(getReturnCarOverCostReqDto);
		GetReturnAndOverFeeDetailVO getReturnAndOverFeeDetailVO = orderCostRemoteService.getGetReturnFeeDetail(getReturnAndOverFeeVO);
		return getReturnAndOverFeeDetailVO;
	}
	
	/**
	 * 获取取还车
	 * @param dto
	 * @param orderType
	 * @param respVo
	 * @return GetReturnCostVO
	 */
	private GetReturnCostVO getGetReturnCostVO(GetReturnCostDTO dto, String orderType, GetReturnCostVO respVo) {
		if(dto == null || dto.getGetReturnResponseVO() == null) {
			return null;
		}
		respVo = respVo == null ? new GetReturnCostVO():respVo;
		GetReturnResponseVO grrv = dto.getGetReturnResponseVO();
		logger.info("GetReturnResponseVO data={}", grrv.toString());
		if(CategoryEnum.LONG_ORDER.getCode().equals(orderType)) {
		    // 长租订单都不收取还车费用
            respVo.setGetCost(STR_ZERO);
            respVo.setReturnCost(STR_ZERO);
        } else {
            respVo.setGetCost(String.valueOf(grrv.getGetFee()));
            respVo.setReturnCost(String.valueOf(grrv.getReturnFee()));
        }
		respVo.setGetShouldCost(String.valueOf(grrv.getGetShouldFee()));
		respVo.setReturnShouldCost(String.valueOf(grrv.getReturnShouldFee()));
	
		return respVo;
	}
	
	/**
	 * 获取超运能
	 * @param overDto
	 * @param orderType
	 * @param respVo
	 * @return GetReturnCostVO
	 */
	private GetReturnCostVO getGetReturnOverCostVO(GetReturnOverCostDTO overDto, String orderType, GetReturnCostVO respVo) {
		if(overDto == null || overDto.getGetReturnOverTransportDTO() == null) {
			return null;
		}
		respVo = respVo == null ? new GetReturnCostVO():respVo;
		GetReturnOverTransportDTO grot = overDto.getGetReturnOverTransportDTO();
		logger.info("GetReturnOverTransportDTO data={}", grot.toString());
		if(grot.getIsGetOverTransport()) {
			respVo.setIsGetOverTransport(YesNoEnum.YES.getType());
		}else {
			respVo.setIsGetOverTransport(YesNoEnum.NO.getType());
		}
		//封装数据
		if(grot.getIsReturnOverTransport()) {
			respVo.setIsReturnOverTransport(YesNoEnum.YES.getType());
		}else {
			respVo.setIsReturnOverTransport(YesNoEnum.NO.getType());
		}
        if(CategoryEnum.LONG_ORDER.getCode().equals(orderType)) {
            // 长租订单都不收取超运能溢价金额
            respVo.setGetOverTransportFee(STR_ZERO);
            respVo.setReturnOverTransportFee(STR_ZERO);
            respVo.setNightGetOverTransportFee(STR_ZERO);
            respVo.setNightReturnOverTransportFee(STR_ZERO);
        } else {
            respVo.setGetOverTransportFee(String.valueOf(grot.getGetOverTransportFee()));
            respVo.setReturnOverTransportFee(String.valueOf(grot.getReturnOverTransportFee()));
            respVo.setNightGetOverTransportFee(String.valueOf(grot.getNightGetOverTransportFee()));
            respVo.setNightReturnOverTransportFee(String.valueOf(grot.getNightReturnOverTransportFee()));
        }
		return respVo;
	}
	
}
