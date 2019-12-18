package com.atzuche.order.rentercost.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDto;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.AbatementAmtDTO;
import com.atzuche.order.rentercost.entity.dto.CostBaseDTO;
import com.atzuche.order.rentercost.entity.dto.DepositAmtDTO;
import com.atzuche.order.rentercost.entity.dto.ExtraDriverDTO;
import com.atzuche.order.rentercost.entity.dto.IllegalDepositAmtDTO;
import com.atzuche.order.rentercost.entity.dto.InsurAmtDTO;
import com.atzuche.order.rentercost.entity.dto.RentAmtDTO;
import com.atzuche.order.rentercost.exception.RenterCostParameterException;
import com.autoyol.platformcost.CommonUtils;
import com.autoyol.platformcost.RenterFeeCalculatorUtils;
import com.autoyol.platformcost.model.CarDepositAmtVO;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.DepositText;
import com.autoyol.platformcost.model.FeeResult;
import com.autoyol.platformcost.model.IllegalDepositConfig;
import com.autoyol.platformcost.model.InsuranceConfig;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RenterOrderCostCombineService {
	
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

	/**
	 * 获取租金对象列表
	 * @param rentAmtDTO
	 * @return List<RenterOrderCostDetailEntity>
	 */
	public List<RenterOrderCostDetailEntity> listRentAmtEntity(RentAmtDTO rentAmtDTO) {
		log.info("getRentAmtEntity rentAmtDTO=[{}]",rentAmtDTO);
		if (rentAmtDTO == null) {
			log.error("getRentAmtEntity 获取租金对象列表rentAmtDTO对象为空");
			Cat.logError("获取租金对象列表rentAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = rentAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getRentAmtEntity 获取租金对象列表rentAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取租金对象列表rentAmtDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		
		List<RenterGoodsPriceDetailDto> dayPriceList = rentAmtDTO.getRenterGoodsPriceDetailDtoList();
		// 按还车时间分组
		Map<LocalDateTime, List<RenterGoodsPriceDetailDto>> dayPriceMap = dayPriceList.stream().collect(Collectors.groupingBy(RenterGoodsPriceDetailDto::getRevertTime));
		dayPriceMap = dayPriceMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		int i = 1;
		List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = new ArrayList<RenterOrderCostDetailEntity>();
		for(Map.Entry<LocalDateTime, List<RenterGoodsPriceDetailDto>> it : dayPriceMap.entrySet()){
			if (i == 1) {
				costBaseDTO.setEndTime(it.getKey());
			} else {
				costBaseDTO.setStartTime(costBaseDTO.getEndTime());
				costBaseDTO.setEndTime(it.getKey());
			}
			renterOrderCostDetailEntityList.add(getRentAmtEntity(costBaseDTO, it.getValue()));
			i++;
		}
		return renterOrderCostDetailEntityList;
	}
	
	public RenterOrderCostDetailEntity getRentAmtEntity(CostBaseDTO costBaseDTO, List<RenterGoodsPriceDetailDto> dayPrices) {
		// TODO 走配置中心获取
		Integer configHours = 8;
		// 数据转化
		List<CarPriceOfDay> carPriceOfDayList = dayPrices.stream().map(dayPrice -> {
			CarPriceOfDay carPriceOfDay = new CarPriceOfDay();
			carPriceOfDay.setCurDate(dayPrice.getCarDay());
			carPriceOfDay.setDayPrice(dayPrice.getCarUnitPrice());
			return carPriceOfDay;
		}).collect(Collectors.toList());
		// 计算租金
		FeeResult feeResult = RenterFeeCalculatorUtils.calRentAmt(costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), configHours, carPriceOfDayList);
		RenterOrderCostDetailEntity result = costBaseConvert(costBaseDTO, feeResult, RenterCashCodeEnum.RENT_AMT);
		return result;
	}
	
	
	
	/**
	 * 获取平台手续费返回结果
	 * @param costBaseDTO
	 * @return RenterOrderCostDetailEntity
	 */
	public RenterOrderCostDetailEntity getServiceChargeFeeEntity(CostBaseDTO costBaseDTO) {
		log.info("getServiceChargeFeeEntity costBaseDTO=[{}]",costBaseDTO);
		if (costBaseDTO == null) {
			log.error("getServiceChargeFeeEntity 获取平台手续费costBaseDTO对象为空");
			Cat.logError("获取平台手续费costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		FeeResult feeResult = RenterFeeCalculatorUtils.calServiceChargeFee();
		RenterOrderCostDetailEntity result = costBaseConvert(costBaseDTO, feeResult, RenterCashCodeEnum.FEE);
		return result;
	}
	
	
	/**
	 * 获取平台保障费返回结果
	 * @param insurAmtDTO
	 * @return RenterOrderCostDetailEntity
	 */
	public RenterOrderCostDetailEntity getInsurAmtEntity(InsurAmtDTO insurAmtDTO) {
		log.info("getInsurAmtEntity insurAmtDTO=[{}]",insurAmtDTO);
		if (insurAmtDTO == null) {
			log.error("getInsurAmtEntity 获取平台保障费insurAmtDTO对象为空");
			Cat.logError("获取平台保障费insurAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = insurAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getInsurAmtEntity 获取平台保障费insurAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取平台保障费insurAmtDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 走配置中心获取
		Integer configHours = 8;
		// TODO 走配置中心获取
		List<InsuranceConfig> insuranceConfigs = null;
		// 指导价
		Integer guidPrice = insurAmtDTO.getGuidPrice();
		if (insurAmtDTO.getInmsrp() != null && insurAmtDTO.getInmsrp() != 0) {
			guidPrice = insurAmtDTO.getInmsrp();
		}
		// 会员系数
		Double coefficient = CommonUtils.getDriveAgeCoefficientByDri(insurAmtDTO.getCertificationTime());
		// 车辆标签系数
		Double easyCoefficient = CommonUtils.getEasyCoefficient(insurAmtDTO.getCarLabelIds());
		FeeResult feeResult = RenterFeeCalculatorUtils.calInsurAmt(costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), 
				insurAmtDTO.getGetCarBeforeTime(), insurAmtDTO.getReturnCarAfterTime(), configHours, guidPrice, coefficient, easyCoefficient, insuranceConfigs);
		RenterOrderCostDetailEntity result = costBaseConvert(costBaseDTO, feeResult, RenterCashCodeEnum.INSURE_TOTAL_PRICES);
		return result;
	}
	
	
	/**
	 * 获取全面保障费返回结果
	 * @param abatementAmtDTO
	 * @return List<RenterOrderCostDetailEntity>
	 */
	public List<RenterOrderCostDetailEntity> listAbatementAmtEntity(AbatementAmtDTO abatementAmtDTO) {
		log.info("listAbatementAmtEntity abatementAmtDTO=[{}]",abatementAmtDTO);
		if (abatementAmtDTO == null) {
			log.error("listAbatementAmtEntity 获取全面保障费abatementAmtDTO对象为空");
			Cat.logError("获取全面保障费abatementAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = abatementAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("listAbatementAmtEntity 获取全面保障费abatementAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取全面保障费abatementAmtDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 走配置中心获取
		Integer configHours = 8;
		// 指导价
		Integer guidPrice = abatementAmtDTO.getGuidPrice();
		if (abatementAmtDTO.getInmsrp() != null && abatementAmtDTO.getInmsrp() != 0) {
			guidPrice = abatementAmtDTO.getInmsrp();
		}
		// 会员系数
		Double coefficient = CommonUtils.getDriveAgeCoefficientByDri(abatementAmtDTO.getCertificationTime());
		// 车辆标签系数
		Double easyCoefficient = CommonUtils.getEasyCoefficient(abatementAmtDTO.getCarLabelIds());
		List<FeeResult> feeResultList = RenterFeeCalculatorUtils.calcAbatementAmt(costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), abatementAmtDTO.getGetCarBeforeTime(), abatementAmtDTO.getReturnCarAfterTime(), configHours, guidPrice, coefficient, easyCoefficient);
		List<RenterOrderCostDetailEntity> resultList = feeResultList.stream().map(fr -> costBaseConvert(costBaseDTO, fr, RenterCashCodeEnum.ABATEMENT_INSURE)).collect(Collectors.toList());
		return resultList;
	}
	
	/**
	 * 获取附加驾驶人费用返回结果
	 * @param extraDriverDTO
	 * @return RenterOrderCostDetailEntity
	 */
	public RenterOrderCostDetailEntity getExtraDriverInsureAmtEntity(ExtraDriverDTO extraDriverDTO) {
		log.info("getExtraDriverInsureAmtEntity extraDriverDTO=[{}]",extraDriverDTO);
		if (extraDriverDTO == null) {
			log.error("getExtraDriverInsureAmtEntity 获取附加驾驶人费用extraDriverDTO对象为空");
			Cat.logError("获取附加驾驶人费用extraDriverDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = extraDriverDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getExtraDriverInsureAmtEntity 获取附加驾驶人费用extraDriverDTO.costBaseDTO对象为空");
			Cat.logError("获取附加驾驶人费用extraDriverDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 走配置中心取单价
		Integer unitExtraDriverInsure = 20;
		List<String> driverIds = extraDriverDTO.getDriverIds();
		Integer extraDriverCount = (driverIds == null || driverIds.isEmpty()) ? 0:driverIds.size();
		FeeResult feeResult = RenterFeeCalculatorUtils.calExtraDriverInsureAmt(unitExtraDriverInsure, extraDriverCount, costBaseDTO.getStartTime(), costBaseDTO.getEndTime());
		RenterOrderCostDetailEntity result = costBaseConvert(costBaseDTO, feeResult, RenterCashCodeEnum.EXTRA_DRIVER_INSURE);
		return result;
	}
	
	
	/**
	 * 获取车辆押金对象
	 * @param depositAmtDTO
	 * @return CarDepositAmtVO
	 */
	public CarDepositAmtVO getCarDepositAmtVO(DepositAmtDTO depositAmtDTO) {
		log.info("getCarDepositAmtVO depositAmtDTO=[{}]",depositAmtDTO);
		if (depositAmtDTO == null) {
			log.error("getCarDepositAmtVO 获取车辆押金对象depositAmtDTO对象为空");
			Cat.logError("获取车辆押金对象depositAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 押金配置列表从配置中心获取
		List<DepositText> depositList = null;
		CarDepositAmtVO carDepositAmtVO = RenterFeeCalculatorUtils.calCarDepositAmt(depositAmtDTO.getInternalStaff(), depositAmtDTO.getCityCode(), 
				depositAmtDTO.getGuidPrice(), depositAmtDTO.getCarBrandTypeRadio(), depositAmtDTO.getCarYearRadio(), 
				depositList, depositAmtDTO.getReliefPercetage());
		return carDepositAmtVO;
	}
	
	
	/**
	 * 获取违章押金
	 * @param illegalDepositAmtDTO
	 * @return Integer
	 */
	public Integer getIllegalDepositAmt(IllegalDepositAmtDTO illDTO) {
		log.info("getIllegalDepositAmt illegalDepositAmtDTO=[{}]",illDTO);
		if (illDTO == null) {
			log.error("getIllegalDepositAmt 获取违章押金illegalDepositAmtDTO对象为空");
			Cat.logError("获取违章押金illegalDepositAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = illDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getIllegalDepositAmt 获取违章押金illegalDepositAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取违章押金illegalDepositAmtDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 特殊城市（逗号分隔的城市编码）从配置中心获取
		String specialCityCodes = null;
		// TODO 特殊车牌合特殊城市对应的特殊押金值从配置中心获取
		Integer specialIllegalDepositAmt = null;
		// TODO 违章押金配置从配置中心获取
		List<IllegalDepositConfig> illegalDepositList = null;
		Integer illegalDepositAmt = RenterFeeCalculatorUtils.calIllegalDepositAmt(illDTO.getInternalStaff(), illDTO.getCityCode(), illDTO.getCarPlateNum(), 
				specialCityCodes, specialIllegalDepositAmt, illegalDepositList, 
				costBaseDTO.getStartTime(), costBaseDTO.getEndTime());
		return illegalDepositAmt;
	}
	
	
	/**
	 * 获取应付
	 * @param orderNo
	 * @param renterOrderNo
	 * @return Integer
	 */
	public Integer getPayable(String orderNo, String renterOrderNo) {
		// 获取费用明细
		List<RenterOrderCostDetailEntity> costList = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
		// 获取补贴明细
		List<RenterOrderSubsidyDetailEntity> subsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
		Integer payable = 0;
		if (costList != null && !costList.isEmpty()) {
			payable += costList.stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
		}
		if (subsidyList != null && !subsidyList.isEmpty()) {
			payable += subsidyList.stream().mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
		}
		return payable;
	}
	
	
	/**
	 * 数据转化
	 * @param costBaseDTO 基本参数
	 * @param feeResult 计算结果对象
	 * @return RenterOrderCostDetailEntity
	 */
	public RenterOrderCostDetailEntity costBaseConvert(CostBaseDTO costBaseDTO, FeeResult feeResult, RenterCashCodeEnum renterCashCodeEnum) {
		if (costBaseDTO == null) {
			return null;
		}
		if (feeResult == null) {
			return null;
		}
		if (renterCashCodeEnum == null) {
			return null;
		}
		RenterOrderCostDetailEntity result = new RenterOrderCostDetailEntity();
		result.setOrderNo(costBaseDTO.getOrderNo());
		result.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		result.setMemNo(costBaseDTO.getMemNo());
		result.setStartTime(costBaseDTO.getStartTime());
		result.setEndTime(costBaseDTO.getEndTime());
		result.setUnitPrice(feeResult.getUnitPrice());
		result.setCount(feeResult.getUnitCount());
		result.setTotalAmount(-feeResult.getTotalFee());
		result.setCostCode(renterCashCodeEnum.getCashNo());
		result.setCostDesc(renterCashCodeEnum.getTxt());
		return result;
	}
}
