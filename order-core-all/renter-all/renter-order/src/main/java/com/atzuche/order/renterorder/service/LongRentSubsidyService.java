package com.atzuche.order.renterorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atzuche.order.rentercost.entity.vo.OwnerCouponLongVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RentAmtDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.entity.vo.HolidayAverageDateTimeVO;
import com.atzuche.order.rentercost.entity.vo.HolidayAverageResultVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponLongReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponLongResVO;
import com.autoyol.platformcost.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LongRentSubsidyService {
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
    private OwnerDiscountCouponService ownerDiscountCouponService;
	@Autowired
	private OwnerCouponLongService ownerCouponLongService;
	@Value("${auto.cost.configHours}")
    private Integer configHours;
	
	/**
	 * 获取长租租金补贴
	 * @param carNo
	 * @param longCouponCode
	 * @param renterOrderCostReqDTO
	 * @param updSubsidyList
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getLongRentAmtSubsidy(String carNo, String longCouponCode, RenterOrderCostReqDTO renterOrderCostReqDTO, List<RenterOrderSubsidyDetailDTO> updSubsidyList) {
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		if (renterOrderCostReqDTO == null) {
			return renterSubsidyList;
		}
		if (StringUtils.isBlank(longCouponCode)) {
			return renterSubsidyList;
		}
		CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("获取长租租金补贴getLongRentAmtSubsidy costBaseDTO is null");
			return renterSubsidyList;
		}
		Map<String,RenterOrderSubsidyDetailDTO> subMap = updSubsidyList == null ? null:updSubsidyList.stream().collect(Collectors.toMap(RenterOrderSubsidyDetailDTO::getSubsidyCostCode, sub -> {return sub;}));
		if (subMap == null || subMap.get(RenterCashCodeEnum.RENT_AMT.getCashNo()) == null) {
			// 获取折扣后日均价对象列表
			List<HolidayAverageResultVO> holidayAvgList = listHolidayAverageResultVO(carNo, longCouponCode, costBaseDTO, renterOrderCostReqDTO.getRentAmtDTO());
			//获取租金
	        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
	        if (renterOrderCostDetailEntities != null && !renterOrderCostDetailEntities.isEmpty()) {
	        	List<RenterOrderSubsidyDetailDTO> rentAmtSubsidyList =
	        			renterOrderCostDetailEntities.stream().map(fr -> getRentAmtSubsidy(costBaseDTO,getAfterRentAmt(fr, holidayAvgList), 
	        					fr,"长租订单车主折扣券抵扣金额")).collect(Collectors.toList());
	        	renterSubsidyList.addAll(rentAmtSubsidyList);
	        }
		}
		return renterSubsidyList.stream().filter(obj -> obj != null).collect(Collectors.toList());
	}
	
	
	/**
	 * 取车费用补贴
	 * @param renterOrderCostReqDTO
	 * @param renterOrderCostRespDTO
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getLongGetAmtSubsidy(RenterOrderCostReqDTO renterOrderCostReqDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		if (renterOrderCostReqDTO == null) {
			return null;
		}
		CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			return null;
		}
		Integer getRealAmt = renterOrderCostRespDTO.getGetRealAmt();
		if (getRealAmt == null || getRealAmt == 0) {
			return null;
		}
		return convertToRenterOrderSubsidyDetailDTO(costBaseDTO, Math.abs(getRealAmt), SubsidyTypeCodeEnum.GET_CAR,
                SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.SRV_GET_COST, "长租订单取车费用补贴");
	}
	
	
	/**
	 * 还车费用补贴
	 * @param renterOrderCostReqDTO
	 * @param renterOrderCostRespDTO
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getLongReturnAmtSubsidy(RenterOrderCostReqDTO renterOrderCostReqDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		if (renterOrderCostReqDTO == null) {
			return null;
		}
		CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			return null;
		}
		Integer returnRealAmt = renterOrderCostRespDTO.getReturnRealAmt();
		if (returnRealAmt == null || returnRealAmt == 0) {
			return null;
		}
		return convertToRenterOrderSubsidyDetailDTO(costBaseDTO, Math.abs(returnRealAmt), SubsidyTypeCodeEnum.RETURN_CAR,
                SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.SRV_RETURN_COST, "长租订单还车费用补贴");
	}
	
	
	/**
	 * 取车超运能费用补贴
	 * @param renterOrderCostReqDTO
	 * @param renterOrderCostRespDTO
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getLongGetOverAmtSubsidy(RenterOrderCostReqDTO renterOrderCostReqDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		if (renterOrderCostReqDTO == null) {
			return null;
		}
		CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			return null;
		}
		Integer getOverAmt = renterOrderCostRespDTO.getGetOverAmt();
		if (getOverAmt == null || getOverAmt == 0) {
			return null;
		}
		return convertToRenterOrderSubsidyDetailDTO(costBaseDTO, Math.abs(getOverAmt), SubsidyTypeCodeEnum.GET_CAR,
                SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT, "长租订单取车超运能费用补贴");
	} 
	
	
	/**
	 * 还车超运能费用补贴
	 * @param renterOrderCostReqDTO
	 * @param renterOrderCostRespDTO
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getLongReturnOverAmtSubsidy(RenterOrderCostReqDTO renterOrderCostReqDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		if (renterOrderCostReqDTO == null) {
			return null;
		}
		CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			return null;
		}
		Integer returnOverAmt = renterOrderCostRespDTO.getReturnOverAmt();
		if (returnOverAmt == null || returnOverAmt == 0) {
			return null;
		}
		return convertToRenterOrderSubsidyDetailDTO(costBaseDTO, Math.abs(returnOverAmt), SubsidyTypeCodeEnum.RETURN_CAR,
                SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT, "长租订单还车超运能费用补贴");
	} 
	
	
	
	/**
	 * 获取折扣后的日均价列表
	 * @param carNo
	 * @param longCouponCode
	 * @param costBaseDTO
	 * @param rentAmtDTO
	 * @return List<HolidayAverageResultVO>
	 */
	private List<HolidayAverageResultVO> listHolidayAverageResultVO(String carNo, String longCouponCode, CostBaseDTO costBaseDTO, RentAmtDTO rentAmtDTO) {
		// 获取日均价对象列表
		List<HolidayAverageDateTimeVO> holidayAvgList = renterOrderCostCombineService.listRentAmtEntityByAverage(rentAmtDTO);
		OwnerCouponLongReqVO req = new OwnerCouponLongReqVO();
		req.setCarNo(carNo);
		req.setCouponCode(longCouponCode);
		req.setOrderNo(costBaseDTO.getOrderNo());
		req.setOwnerUnitPriceVOS(holidayAvgList);
		req.setRenterMemNo(costBaseDTO.getMemNo());
		req.setRentTime(CommonUtils.formatTime(costBaseDTO.getStartTime(), CommonUtils.FORMAT_STR_DEFAULT));
		req.setRevertTime(CommonUtils.formatTime(costBaseDTO.getEndTime(), CommonUtils.FORMAT_STR_DEFAULT));
        OwnerCouponLongVO res = ownerDiscountCouponService.getLongOwnerCoupon(req);
		if (res != null) {
			saveOwnerCouponLongBatch(longCouponCode, costBaseDTO, res);
			return res.getOwnerUnitPriceRespVOS();
		}
		return null;
	}
	
	
	/**
	 * 获取折扣后的总租金
	 * @param rentAmtEntity
	 * @param holidayAvgList
	 * @return Integer
	 */
	private Integer getAfterRentAmt(RenterOrderCostDetailEntity rentAmtEntity, List<HolidayAverageResultVO> holidayAvgList) {
		if (rentAmtEntity == null || holidayAvgList == null || holidayAvgList.isEmpty()) {
			return null;
		}
		Integer holidayAverage = 0;
		String revertTime = CommonUtils.formatTime(rentAmtEntity.getEndTime(), CommonUtils.FORMAT_STR_DEFAULT);
		for (HolidayAverageResultVO ha:holidayAvgList) {
			if (revertTime.equals(ha.getRevertTime())) {
				holidayAverage = ha.getActRentUnitPriceAmt();
				break;
			}
		}
		// 计算总租期
		Double rentDays = CommonUtils.getRentDays(rentAmtEntity.getStartTime(), rentAmtEntity.getEndTime(), configHours);
		Integer rentAmt = new BigDecimal(holidayAverage*rentDays).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		return rentAmt;
	}
	
	
	/**
	 * 获取长租租金补贴
	 * @param costBaseDTO
	 * @param abatementSubsidyEntity
	 * @param insureDiscount
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getRentAmtSubsidy(CostBaseDTO costBaseDTO, Integer afterRentAmt,
                                                           RenterOrderCostDetailEntity rentAmtEntity, String subsidyDesc) {
		if (afterRentAmt == null || rentAmtEntity == null) {
			return null;
		}
		Integer rentAmt = rentAmtEntity.getTotalAmount();
        if (rentAmt != null && Math.abs(rentAmt) > Math.abs(afterRentAmt)) {
        	Integer subsidyAmount = Math.abs(rentAmt) - Math.abs(afterRentAmt);
        	// 产生补贴
        	return convertToRenterOrderSubsidyDetailDTO(costBaseDTO, subsidyAmount, SubsidyTypeCodeEnum.RENT_AMT,
                    SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.RENT_AMT, subsidyDesc);
        }
        log.info("获取长租租金补贴getRentAmtSubsidy rentAmt:[{}],afterRentAmt:[{}]", rentAmt, afterRentAmt);
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
	
	
	/**
	 * 数据转换
	 * @param longCouponCode
	 * @param costBaseDTO
	 * @param res
	 * @return List<OwnerCouponLongEntity>
	 */
	public List<OwnerCouponLongEntity> listOwnerCouponLongEntity(String longCouponCode, CostBaseDTO costBaseDTO, OwnerCouponLongVO res) {
		if (costBaseDTO == null || res == null) {
			return null;
		}
		List<HolidayAverageResultVO> ownerUnitPriceRespVOS = res.getOwnerUnitPriceRespVOS();
		if (ownerUnitPriceRespVOS == null || ownerUnitPriceRespVOS.isEmpty()) {
			return null;
		}
		List<OwnerCouponLongEntity> list = new ArrayList<OwnerCouponLongEntity>();
		for (HolidayAverageResultVO ha:ownerUnitPriceRespVOS) {
			OwnerCouponLongEntity entity = new OwnerCouponLongEntity();
			entity.setActUnitPrice(ha.getActRentUnitPriceAmt());
			entity.setCouponCode(longCouponCode);
			entity.setDiscounRatio(ha.getDiscounRatio());
			entity.setDiscountDesc(res.getDiscountDesc());
			entity.setOrderNo(costBaseDTO.getOrderNo());
			entity.setOriginalUnitPrice(ha.getRentOriginalUnitPriceAmt());
			entity.setReductionPrice(ha.getReductionAmt());
			entity.setRenterMemNo(costBaseDTO.getMemNo());
			entity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
			list.add(entity);
		}
		return list;
	}
	
	
	/**
	 * 比例保存长租折扣信息
	 * @param longCouponCode
	 * @param costBaseDTO
	 * @param res
	 */
	public void saveOwnerCouponLongBatch(String longCouponCode, CostBaseDTO costBaseDTO, OwnerCouponLongVO res) {
		List<OwnerCouponLongEntity> list = listOwnerCouponLongEntity(longCouponCode, costBaseDTO, res);
		ownerCouponLongService.saveOwnerCouponLongBatch(list);
	}
}