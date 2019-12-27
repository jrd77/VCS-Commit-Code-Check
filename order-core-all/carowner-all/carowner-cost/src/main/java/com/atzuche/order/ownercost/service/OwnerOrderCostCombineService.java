package com.atzuche.order.ownercost.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.OilAmtDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.enums.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.exception.OwnerCostParameterException;
import com.autoyol.platformcost.OwnerFeeCalculatorUtils;
import com.autoyol.platformcost.model.CarGpsRule;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.FeeResult;
import com.autoyol.platformcost.model.OilAverageCostBO;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerOrderCostCombineService {
	
	/**
	 * 获取租金对象列表
	 * @param rentAmtDTO 租金对象
	 * @return List<OwnerOrderPurchaseDetailEntity>
	 */
	public List<OwnerOrderPurchaseDetailEntity> listOwnerRentAmtEntity(CostBaseDTO costBaseDTO, List<OwnerGoodsPriceDetailDTO> dayPriceList) {
		log.info("listOwnerRentAmtEntity costBaseDTO=[{}], ownerGoodsPriceDetailDTOList=[{}]",costBaseDTO,dayPriceList);
		if (costBaseDTO == null) {
			log.error("listOwnerRentAmtEntity 获取租金对象列表costBaseDTO对象为空");
			Cat.logError("获取租金对象列表costBaseDTO对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		if (dayPriceList == null) {
			log.error("listOwnerRentAmtEntity 获取租金对象列表ownerGoodsPriceDetailDTOList对象为空");
			Cat.logError("获取租金对象列表ownerGoodsPriceDetailDTOList对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		
		// 按还车时间分组
		Map<LocalDateTime, List<OwnerGoodsPriceDetailDTO>> dayPriceMap = dayPriceList.stream().collect(Collectors.groupingBy(OwnerGoodsPriceDetailDTO::getRevertTime));
		dayPriceMap = dayPriceMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		int i = 1;
		List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailEntityList = new ArrayList<OwnerOrderPurchaseDetailEntity>();
		for(Map.Entry<LocalDateTime, List<OwnerGoodsPriceDetailDTO>> it : dayPriceMap.entrySet()){
			if (i == 1) {
				costBaseDTO.setEndTime(it.getKey());
			} else {
				costBaseDTO.setStartTime(costBaseDTO.getEndTime());
				costBaseDTO.setEndTime(it.getKey());
			}
			ownerOrderPurchaseDetailEntityList.add(getRentAmtEntity(costBaseDTO, it.getValue()));
			i++;
		}
		return ownerOrderPurchaseDetailEntityList;
	}
	
	/**
	 * 获取租金
	 */
	public OwnerOrderPurchaseDetailEntity getRentAmtEntity(CostBaseDTO costBaseDTO, List<OwnerGoodsPriceDetailDTO> dayPrices) {
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
		FeeResult feeResult = OwnerFeeCalculatorUtils.calRentAmt(costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), configHours, carPriceOfDayList);
		OwnerOrderPurchaseDetailEntity result = costBaseConvert(costBaseDTO, feeResult, OwnerCashCodeEnum.RENT_AMT);
		return result;
	}
	
	
	/**
	 * 获取车主超里程费用
	 * @param mileageAmtDTO 超里程对象
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderPurchaseDetailEntity getMileageAmtEntity(MileageAmtDTO mileageAmtDTO) {
		log.info("getMileageAmtEntity mileageAmtDTO=[{}]",mileageAmtDTO);
		if (mileageAmtDTO == null) {
			log.error("getMileageAmtEntity 获取超里程费用mileageAmtDTO对象为空");
			Cat.logError("获取超里程费用mileageAmtDTO对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		CostBaseDTO costBaseDTO = mileageAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getMileageAmtEntity 获取超里程费用mileageAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取超里程费用mileageAmtDTO.costBaseDTO对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		// TODO 走配置中心获取
		Integer configHours = 8;
		Integer mileageAmt = OwnerFeeCalculatorUtils.calMileageAmt(mileageAmtDTO.getCarOwnerType(), mileageAmtDTO.getDayMileage(), mileageAmtDTO.getGuideDayPrice(), 
				mileageAmtDTO.getGetmileage(), mileageAmtDTO.getReturnMileage(), costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), configHours);
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(mileageAmt);
		feeResult.setUnitCount(1.0);
		feeResult.setUnitPrice(mileageAmt);
		OwnerOrderPurchaseDetailEntity result = costBaseConvert(costBaseDTO, feeResult, OwnerCashCodeEnum.MILEAGE_COST_OWNER);
		return result;
	}
	
	
	/**
	 * 获取车主油费
	 * @param oilAmtDTO 油费对象 
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderPurchaseDetailEntity getOilAmtEntity(OilAmtDTO oilAmtDTO) {
		log.info("getOilAmtEntity oilAmtDTO=[{}]",oilAmtDTO);
		if (oilAmtDTO == null) {
			log.error("getOilAmtEntity 获取租客油费oilAmtDTO对象为空");
			Cat.logError("获取租客油费oilAmtDTO对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		CostBaseDTO costBaseDTO = oilAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getOilAmtEntity 获取租客油费oilAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取租客油费oilAmtDTO.costBaseDTO对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		// TODO 走配置中心获取
		List<OilAverageCostBO> oilAverageList = null;
		Integer oilAmt = OwnerFeeCalculatorUtils.calOilAmt(oilAmtDTO.getCarOwnerType(), oilAmtDTO.getCityCode(), oilAmtDTO.getOilVolume(), oilAmtDTO.getEngineType(), 
				oilAmtDTO.getGetOilScale(), oilAmtDTO.getReturnOilScale(), oilAverageList, oilAmtDTO.getOilScaleDenominator());
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(oilAmt);
		feeResult.setUnitCount(1.0);
		feeResult.setUnitPrice(oilAmt);
		OwnerOrderPurchaseDetailEntity result = costBaseConvert(costBaseDTO, feeResult, OwnerCashCodeEnum.OIL_COST_OWNER);
		return result;
	}
	
	
	/**
	 * 获取gps服务费
	 * @param costBaseDTO 基本信息
	 * @param lsGpsSerialNumber 车辆安装gps序列号列表
	 * @return List<OwnerOrderPurchaseDetailEntity>
	 */
	public List<OwnerOrderPurchaseDetailEntity> getGpsServiceAmtEntity(CostBaseDTO costBaseDTO, List<Integer> lsGpsSerialNumber) {
		log.info("getGpsServiceAmtEntity oilAmtDTO=[{}], lsGpsSerialNumber=[{}]",costBaseDTO,lsGpsSerialNumber);
		if (costBaseDTO == null) {
			log.error("getGpsServiceAmtEntity 获取gps服务费costBaseDTO对象为空");
			Cat.logError("获取gps服务费costBaseDTO对象为空", new OwnerCostParameterException());
			throw new OwnerCostParameterException();
		}
		// TODO gps服务费计费规则信息 走配置中心获取
		List<CarGpsRule> lsRules = null;
		List<FeeResult> feeList = OwnerFeeCalculatorUtils.calGpsServiceAmt(lsRules, lsGpsSerialNumber, costBaseDTO.getStartTime(), costBaseDTO.getEndTime());
		if (feeList == null || feeList.isEmpty()) {
			return null;
		}
		return feeList.stream().map(fr -> costBaseConvert(costBaseDTO, fr, OwnerCashCodeEnum.GPS_SERVICE_AMT)).collect(Collectors.toList());
	}
	
	
	/**
	 * 计算车主取车服务费
	 * @param costBaseDTO 基本信息
	 * @param carOwnerType 车辆类型
	 * @param srvGetFlag 取车标志
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderIncrementDetailEntity getOwnerSrvGetAmtEntity(CostBaseDTO costBaseDTO, Integer carOwnerType, Integer srvGetFlag) {
		Integer ownerSrvGetAmt = OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(carOwnerType, srvGetFlag);
		FeeResult feeResult = new FeeResult(ownerSrvGetAmt, 1.0, ownerSrvGetAmt);
		OwnerOrderIncrementDetailEntity result = costBaseConvertIncrement(costBaseDTO, feeResult, OwnerCashCodeEnum.SRV_GET_COST_OWNER);
		return result;
	}
	
	
	/**
	 * 计算车主还车服务费
	 * @param costBaseDTO 基本信息
	 * @param carOwnerType 车辆类型
	 * @param srvReturnFlag 还车标志
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderIncrementDetailEntity getOwnerSrvReturnAmtEntity(CostBaseDTO costBaseDTO, Integer carOwnerType, Integer srvReturnFlag) {
		Integer ownerSrvReturnAmt = OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(carOwnerType, srvReturnFlag);
		FeeResult feeResult = new FeeResult(ownerSrvReturnAmt, 1.0, ownerSrvReturnAmt);
		OwnerOrderIncrementDetailEntity result = costBaseConvertIncrement(costBaseDTO, feeResult, OwnerCashCodeEnum.SRV_RETURN_COST_OWNER);
		return result;
	}
	
	
	/**
	 * 车主端平台服务费
	 * @param costBaseDTO 基本信息
	 * @param rentAmt 租金
	 * @param serviceProportion 服务费比例
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderPurchaseDetailEntity getServiceExpense(CostBaseDTO costBaseDTO, Integer rentAmt, Integer serviceProportion) {
		Integer serviceExpense = OwnerFeeCalculatorUtils.calServiceExpense(rentAmt, serviceProportion);
		FeeResult feeResult = new FeeResult(serviceExpense, 1.0, serviceExpense);
		OwnerOrderPurchaseDetailEntity result = costBaseConvert(costBaseDTO, feeResult, OwnerCashCodeEnum.SERVICE_CHARGE);
		return result;
	}
	
	/**
	 * 车主端代管车服务费
	 * @param costBaseDTO 基本信息
	 * @param rentAmt 租金
	 * @param proxyProportion 代管车服务费比例
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderPurchaseDetailEntity getProxyExpense(CostBaseDTO costBaseDTO, Integer rentAmt, Integer proxyProportion) {
		Integer proxyExpense = OwnerFeeCalculatorUtils.calProxyExpense(rentAmt, proxyProportion);
		FeeResult feeResult = new FeeResult(proxyExpense, 1.0, proxyExpense);
		OwnerOrderPurchaseDetailEntity result = costBaseConvert(costBaseDTO, feeResult, OwnerCashCodeEnum.PROXY_CHARGE);
		return result;
	}
	
	
	/**
	 * 数据转化
	 * @param costBaseDTO 基本参数
	 * @param feeResult 计算结果对象
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderPurchaseDetailEntity costBaseConvert(CostBaseDTO costBaseDTO, FeeResult feeResult, OwnerCashCodeEnum ownerCashCodeEnum) {
		if (costBaseDTO == null) {
			return null;
		}
		if (feeResult == null) {
			return null;
		}
		if (ownerCashCodeEnum == null) {
			return null;
		}
		Integer totalFee = feeResult.getTotalFee() == null ? 0:feeResult.getTotalFee();
		OwnerOrderPurchaseDetailEntity result = new OwnerOrderPurchaseDetailEntity();
		result.setOrderNo(costBaseDTO.getOrderNo());
		result.setOwnerOrderNo(costBaseDTO.getOwnerOrderNo());
		result.setMemNo(costBaseDTO.getMemNo());
		result.setUnitPrice(feeResult.getUnitPrice());
		result.setCount(feeResult.getUnitCount());
		result.setTotalAmount(totalFee);
		result.setCostCode(ownerCashCodeEnum.getCashNo());
		result.setCostCodeDesc(ownerCashCodeEnum.getTxt());
		return result;
	}
	
	
	/**
	 * 数据转化（增值服务）
	 * @param costBaseDTO 基本参数
	 * @param feeResult 计算结果对象
	 * @return OwnerOrderPurchaseDetailEntity
	 */
	public OwnerOrderIncrementDetailEntity costBaseConvertIncrement(CostBaseDTO costBaseDTO, FeeResult feeResult, OwnerCashCodeEnum ownerCashCodeEnum) {
		if (costBaseDTO == null) {
			return null;
		}
		if (feeResult == null) {
			return null;
		}
		if (ownerCashCodeEnum == null) {
			return null;
		}
		Integer totalFee = feeResult.getTotalFee() == null ? 0:feeResult.getTotalFee();
		OwnerOrderIncrementDetailEntity result = new OwnerOrderIncrementDetailEntity();
		result.setOrderNo(costBaseDTO.getOrderNo());
		result.setOwnerOrderNo(costBaseDTO.getOwnerOrderNo());
		result.setMemNo(costBaseDTO.getMemNo());
		result.setUnitPrice(feeResult.getUnitPrice());
		result.setCount(feeResult.getUnitCount());
		result.setTotalAmount(-totalFee);
		result.setCostCode(ownerCashCodeEnum.getCashNo());
		result.setCostDesc(ownerCashCodeEnum.getTxt());
		return result;
	}
}
