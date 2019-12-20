package com.atzuche.order.rentercost.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.AbatementAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.DepositAmtDTO;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.entity.dto.IllegalDepositAmtDTO;
import com.atzuche.order.commons.entity.dto.InsurAmtDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.OilAmtDTO;
import com.atzuche.order.commons.entity.dto.RentAmtDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.exception.RenterCostParameterException;
import com.autoyol.platformcost.CommonUtils;
import com.autoyol.platformcost.RenterFeeCalculatorUtils;
import com.autoyol.platformcost.model.CarDepositAmtVO;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.DepositText;
import com.autoyol.platformcost.model.FeeResult;
import com.autoyol.platformcost.model.IllegalDepositConfig;
import com.autoyol.platformcost.model.InsuranceConfig;
import com.autoyol.platformcost.model.OilAverageCostBO;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RenterOrderCostCombineService {
	
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	@Autowired
	private OrderConsoleCostDetailService orderConsoleCostDetailService;
	
	public static final List<RenterCashCodeEnum> RENTERCASHCODEENUM_LIST = new ArrayList<RenterCashCodeEnum>() {

		private static final long serialVersionUID = 1L;

	{
        add(RenterCashCodeEnum.RENT_AMT);
        add(RenterCashCodeEnum.INSURE_TOTAL_PRICES);
        add(RenterCashCodeEnum.ABATEMENT_INSURE);
        add(RenterCashCodeEnum.FEE);
        add(RenterCashCodeEnum.SRV_GET_COST);
        add(RenterCashCodeEnum.SRV_RETURN_COST);
        add(RenterCashCodeEnum.MILEAGE_COST_RENTER);
        add(RenterCashCodeEnum.OIL_COST_RENTER);
        add(RenterCashCodeEnum.EXTRA_DRIVER_INSURE);
    }};

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
		
		List<RenterGoodsPriceDetailDTO> dayPriceList = rentAmtDTO.getRenterGoodsPriceDetailDTOList();
		// 按还车时间分组
		Map<LocalDateTime, List<RenterGoodsPriceDetailDTO>> dayPriceMap = dayPriceList.stream().collect(Collectors.groupingBy(RenterGoodsPriceDetailDTO::getRevertTime));
		dayPriceMap = dayPriceMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		int i = 1;
		List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = new ArrayList<RenterOrderCostDetailEntity>();
		for(Map.Entry<LocalDateTime, List<RenterGoodsPriceDetailDTO>> it : dayPriceMap.entrySet()){
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
	
	public RenterOrderCostDetailEntity getRentAmtEntity(CostBaseDTO costBaseDTO, List<RenterGoodsPriceDetailDTO> dayPrices) {
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
	 * 获取超里程费用
	 * @param mileageAmtDTO
	 * @return RenterOrderCostDetailEntity
	 */
	public RenterOrderCostDetailEntity getMileageAmtEntity(MileageAmtDTO mileageAmtDTO) {
		log.info("getMileageAmtEntity mileageAmtDTO=[{}]",mileageAmtDTO);
		if (mileageAmtDTO == null) {
			log.error("getMileageAmtEntity 获取超里程费用mileageAmtDTO对象为空");
			Cat.logError("获取超里程费用mileageAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = mileageAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getMileageAmtEntity 获取超里程费用mileageAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取超里程费用mileageAmtDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 走配置中心获取
		Integer configHours = 8;
		Integer mileageAmt = RenterFeeCalculatorUtils.calMileageAmt(mileageAmtDTO.getDayMileage(), mileageAmtDTO.getGuideDayPrice(), 
				mileageAmtDTO.getGetmileage(), mileageAmtDTO.getReturnMileage(), costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), configHours);
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(mileageAmt);
		feeResult.setUnitCount(1.0);
		feeResult.setUnitPrice(mileageAmt);
		RenterOrderCostDetailEntity result = costBaseConvert(costBaseDTO, feeResult, RenterCashCodeEnum.MILEAGE_COST_RENTER);
		return result;
	}
	
	
	/**
	 * 获取租客油费
	 * @param oilAmtDTO
	 * @return RenterOrderCostDetailEntity
	 */
	public RenterOrderCostDetailEntity getOilAmtEntity(OilAmtDTO oilAmtDTO) {
		log.info("getOilAmtEntity oilAmtDTO=[{}]",oilAmtDTO);
		if (oilAmtDTO == null) {
			log.error("getOilAmtEntity 获取租客油费oilAmtDTO对象为空");
			Cat.logError("获取租客油费oilAmtDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		CostBaseDTO costBaseDTO = oilAmtDTO.getCostBaseDTO();
		if (costBaseDTO == null) {
			log.error("getOilAmtEntity 获取租客油费oilAmtDTO.costBaseDTO对象为空");
			Cat.logError("获取租客油费oilAmtDTO.costBaseDTO对象为空", new RenterCostParameterException());
			throw new RenterCostParameterException();
		}
		// TODO 走配置中心获取
		List<OilAverageCostBO> oilAverageList = null;
		Integer oilAmt = RenterFeeCalculatorUtils.calOilAmt(oilAmtDTO.getCityCode(), oilAmtDTO.getOilVolume(), oilAmtDTO.getEngineType(), 
				oilAmtDTO.getGetOilScale(), oilAmtDTO.getReturnOilScale(), oilAverageList, oilAmtDTO.getOilScaleDenominator());
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(oilAmt);
		feeResult.setUnitCount(1.0);
		feeResult.setUnitPrice(oilAmt);
		RenterOrderCostDetailEntity result = costBaseConvert(costBaseDTO, feeResult, RenterCashCodeEnum.OIL_COST_RENTER);
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
	 * 获取租客应付(正常订单流转)
	 * @param orderNo 主订单号
	 * @param renterOrderNo 租客订单号
	 * @return Integer
	 */
	public Integer getPayable(String orderNo, String renterOrderNo, String memNo) {
		// 获取费用明细
		List<RenterOrderCostDetailEntity> costList = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
		// 获取补贴明细
		List<RenterOrderSubsidyDetailEntity> subsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
		// 罚金
		List<RenterOrderFineDeatailEntity> fineList = renterOrderFineDeatailService.listRenterOrderFineDeatail(orderNo, renterOrderNo);
		// 管理后台补贴
		List<OrderConsoleCostDetailEntity> consoleCostList = orderConsoleCostDetailService.listOrderConsoleCostDetail(orderNo,memNo);
		Integer payable = 0;
		if (costList != null && !costList.isEmpty()) {
			payable += costList.stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
		}
		if (subsidyList != null && !subsidyList.isEmpty()) {
			payable += subsidyList.stream().mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
		}
		if (fineList != null && !fineList.isEmpty()) {
			payable += fineList.stream().mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
		}
		if (consoleCostList != null && !consoleCostList.isEmpty()) {
			payable += consoleCostList.stream().mapToInt(OrderConsoleCostDetailEntity::getSubsidyAmount).sum();
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
		Integer totalFee = feeResult.getTotalFee() == null ? 0:feeResult.getTotalFee();
		if (RENTERCASHCODEENUM_LIST.contains(renterCashCodeEnum)) {
			totalFee = -totalFee;
		}
		RenterOrderCostDetailEntity result = new RenterOrderCostDetailEntity();
		result.setOrderNo(costBaseDTO.getOrderNo());
		result.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		result.setMemNo(costBaseDTO.getMemNo());
		result.setStartTime(costBaseDTO.getStartTime());
		result.setEndTime(costBaseDTO.getEndTime());
		result.setUnitPrice(feeResult.getUnitPrice());
		result.setCount(feeResult.getUnitCount());
		result.setTotalAmount(totalFee);
		result.setCostCode(renterCashCodeEnum.getCashNo());
		result.setCostDesc(renterCashCodeEnum.getTxt());
		return result;
	}
}
