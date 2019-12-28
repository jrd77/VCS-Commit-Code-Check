package com.atzuche.order.rentercost.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.ChannelNameTypeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.entity.dto.*;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverTransportDTO;
import com.atzuche.order.rentercost.entity.vo.GetReturnResponseVO;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.exception.GetReturnCostErrorException;
import com.atzuche.order.rentercost.exception.*;
import com.autoyol.car.api.feign.api.GetBackCityLimitFeignApi;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.feeservice.api.FetchBackCarFeeFeignService;
import com.autoyol.feeservice.api.request.GetFbcFeeConfigRequest;
import com.autoyol.feeservice.api.request.GetFbcFeeRequest;
import com.autoyol.feeservice.api.request.GetFbcFeeRequestDetail;
import com.autoyol.feeservice.api.response.PriceFbcFeeResponseDetail;
import com.autoyol.feeservice.api.response.PriceGetFbcFeeResponse;
import com.autoyol.feeservice.api.vo.pricefetchback.PriceCarHumanFeeRule;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
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
import org.springframework.web.client.RestTemplate;

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
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FetchBackCarFeeFeignService fetchBackCarFeeFeignService;
    @Autowired
    private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    @Autowired
    private OrderSupplementDetailService orderSupplementDetailService;

    @Autowired
    private GetBackCityLimitFeignApi getBackCityLimitFeignApi;


    private static final Integer [] ORDER_TYPES = {1,2};
	
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
	
	private RenterOrderCostDetailEntity getRentAmtEntity(CostBaseDTO costBaseDTO, List<RenterGoodsPriceDetailDTO> dayPrices) {
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
				depositAmtDTO.getSurplusPrice(), depositAmtDTO.getCarBrandTypeRadio(), depositAmtDTO.getCarYearRadio(), 
				depositList, depositAmtDTO.getReliefPercetage());
		return carDepositAmtVO;
	}
	
	
	/**
	 * 获取违章押金
	 * @param illDTO
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
	 * 获取应付记录
	 * @param orderNo 主订单号
	 * @param renterOrderNo 租客子订单号
	 * @param memNo 会员号
	 * @return List<PayableVO>
	 */
	public List<PayableVO> listPayableVO(String orderNo, String renterOrderNo, String memNo) {
		List<PayableVO> payableList = new ArrayList<PayableVO>();
		if (StringUtils.isNotBlank(renterOrderNo)) {
			payableList.add(getPayable(orderNo, renterOrderNo, memNo));
		}
		List<OrderSupplementDetailEntity> supplementList = orderSupplementDetailService.listOrderSupplementDetailByOrderNoAndMemNo(orderNo, memNo);
		if (supplementList != null && !supplementList.isEmpty()) {
			List<PayableVO> suppList = supplementList.stream().map(supplement -> {
				PayableVO payableVO = new PayableVO();
				payableVO.setAmt(supplement.getAmt());
				payableVO.setOrderNo(orderNo);
				payableVO.setTitle(supplement.getTitle());
				payableVO.setType(2);
				payableVO.setUniqueNo(String.valueOf(supplement.getId()));
				return payableVO;
			}).collect(Collectors.toList());
			payableList.addAll(suppList);
		}
		return payableList;
	}
	
	
	/**
	 * 获取租客应付(正常订单流转)
	 * @param orderNo 主订单号
	 * @param renterOrderNo 租客订单号
	 * @return Integer
	 */
	public PayableVO getPayable(String orderNo, String renterOrderNo, String memNo) {
		// 获取费用明细
		List<RenterOrderCostDetailEntity> costList = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
		// 获取补贴明细
		List<RenterOrderSubsidyDetailEntity> subsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
		// 罚金
		List<RenterOrderFineDeatailEntity> fineList = renterOrderFineDeatailService.listRenterOrderFineDeatail(orderNo, renterOrderNo);
		// 管理后台补贴
		List<OrderConsoleCostDetailEntity> consoleCostList = orderConsoleCostDetailService.listOrderConsoleCostDetail(orderNo,memNo);
		// 获取租客全局罚金
		List<ConsoleRenterOrderFineDeatailEntity> consoleFineList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo, memNo);
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
		if (consoleFineList != null && !consoleFineList.isEmpty()) {
			payable += consoleFineList.stream().mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
		}
		PayableVO payableVO = new PayableVO();
		payableVO.setAmt(payable);
		payableVO.setOrderNo(orderNo);
		payableVO.setTitle("修改订单补付");
		payableVO.setType(1);
		payableVO.setUniqueNo(renterOrderNo);
		return payableVO;
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

	
    /*
     * @Author ZhangBin
     * @Date 2019/12/26 10:50
     * @Description: 计算取还车费用
     *
     **/
    public GetReturnCostDTO getReturnCarCost(GetReturnCarCostReqDto getReturnCarCostReqDto) {
        GetReturnCostDTO getReturnCostDto = new GetReturnCostDTO();
        List<RenterOrderCostDetailEntity> listCostDetail = new ArrayList<>();
        List<RenterOrderSubsidyDetailDTO> listCostSubsidy = new ArrayList<>();
        GetReturnResponseVO getReturnResponse = new GetReturnResponseVO();

        CostBaseDTO costBaseDTO = getReturnCarCostReqDto.getCostBaseDTO();
        // 城市编码
        Integer cityCode = getReturnCarCostReqDto.getCityCode();
        // 订单来源
        Integer source = getReturnCarCostReqDto.getSource();
        // 场景号
        String entryCode = getReturnCarCostReqDto.getEntryCode();
        // 取车经度
        String srvGetLon = getReturnCarCostReqDto.getSrvGetLon();
        // 取车纬度
        String srvGetLat = getReturnCarCostReqDto.getSrvGetLat();
        // 还车经度
        String srvReturnLon = getReturnCarCostReqDto.getSrvReturnLon();
        // 还车纬度
        String srvReturnLat = getReturnCarCostReqDto.getSrvReturnLat();
        // 车辆经度
        String carLon = getReturnCarCostReqDto.getCarLon();
        // 车辆纬度
        String carLat = getReturnCarCostReqDto.getCarLat();
        boolean getFlag = StringUtils.isBlank(srvGetLon) || StringUtils.isBlank(srvGetLat) || "0.0".equalsIgnoreCase(srvGetLon) || "0.0".equalsIgnoreCase(srvGetLat);
        boolean returnFlag = StringUtils.isBlank(srvReturnLon) || StringUtils.isBlank(srvReturnLat) || "0.0".equalsIgnoreCase(srvReturnLon) || "0.0".equalsIgnoreCase(srvReturnLat);
        CityDTO cityDTO = null;
        if (getFlag || returnFlag) {
            //TODO 配置中获取
            //cityDTO = cityMapper.getCityLonAndLatByCode(cityCode);
        }
        if (getFlag && cityDTO != null) {
            srvGetLon = cityDTO.getLon();
            srvGetLat = cityDTO.getLat();
        }
        if (returnFlag && cityDTO != null) {
            srvReturnLon = cityDTO.getLon();
            srvReturnLat = cityDTO.getLat();
        }
        //获取取车的距离
        Float getDistance = this.getRealDistance(srvGetLon, srvGetLat, carLon, carLat);
        //获取还车的距离
        Float returnDistance = this.getRealDistance(srvReturnLon, srvReturnLat, carLon, carLat);
        String channelCode = getChannelCodeByEntryCode(entryCode).getTypeCode();
        if(getReturnCarCostReqDto.getIsPackageOrder() != null && getReturnCarCostReqDto.getIsPackageOrder()){
            channelCode = getChannelCode(source).getTypeCode();
        }
        // 租金+保险+不计免赔+手续费
        Integer sumJudgeFreeFee = getReturnCarCostReqDto.getSumJudgeFreeFee();
        // 订单的租金，平台保障费，全面保障费，平台手续费总和小于300，则取还车服务不享受免费
        sumJudgeFreeFee = sumJudgeFreeFee == null ? 0:sumJudgeFreeFee;
        String sumJudgeFreeFeeStr = String.valueOf(sumJudgeFreeFee);
        log.info("取还车费用计算，租金+保险+不计免赔+手续费 sumJudgeFreeFee=[{}]", sumJudgeFreeFee);



        GetFbcFeeRequest getFbcFeeRequest = new GetFbcFeeRequest();
        List<GetFbcFeeRequestDetail> reqList = new ArrayList<>();

        GetFbcFeeRequestDetail getCost = new GetFbcFeeRequestDetail();
        getCost.setChannelName(channelCode);
        getCost.setRequestTime(LocalDateTimeUtils.getNowDateLong());
        getCost.setGetReturnType("return");
        getCost.setGetReturnTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(costBaseDTO.getEndTime())));
        getCost.setCityId(String.valueOf(cityCode));
        getCost.setOrderType(this.getIsPackageOrder(getReturnCarCostReqDto.getIsPackageOrder()));
        getCost.setDistance(String.valueOf(returnDistance));
        if(returnFlag && cityDTO !=null) {
            getCost.setRenterLocation(cityDTO.getLon()+","+ cityDTO.getLat());
        } else {
            getCost.setRenterLocation(srvReturnLon+","+srvReturnLat);
        }
        getCost.setSumJudgeFreeFee(sumJudgeFreeFeeStr);
        reqList.add(getCost);


        GetFbcFeeRequestDetail returnCost = new GetFbcFeeRequestDetail();
        returnCost.setChannelName(channelCode);
        returnCost.setRequestTime(LocalDateTimeUtils.getNowDateLong());
        returnCost.setGetReturnType("return");
        returnCost.setGetReturnTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(costBaseDTO.getEndTime())));
        returnCost.setCityId(String.valueOf(cityCode));
        returnCost.setOrderType(this.getIsPackageOrder(getReturnCarCostReqDto.getIsPackageOrder()));
        returnCost.setDistance(String.valueOf(returnDistance));
        if(returnFlag && cityDTO !=null) {
            returnCost.setRenterLocation(cityDTO.getLon()+","+ cityDTO.getLat());
        } else {
            returnCost.setRenterLocation(srvReturnLon+","+srvReturnLat);
        }
        returnCost.setSumJudgeFreeFee(sumJudgeFreeFeeStr);
        reqList.add(returnCost);

        getFbcFeeRequest.setReq(reqList);
        ResponseData<PriceGetFbcFeeResponse> responseData = null;
        Transaction t = Cat.newTransaction(com.atzuche.order.commons.CatConstants.FEIGN_CALL, "取还车费用");
        try{
            log.info("Feign 获取取还车费用入参:[{}]",JSON.toJSONString(getFbcFeeRequest));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FetchBackCarFeeFeignService.getFbcFee");
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(getFbcFeeRequest));
            responseData = fetchBackCarFeeFeignService.getFbcFee(getFbcFeeRequest);
            log.info("Feign 获取取还车费用结果:[{}],获取取还车费用入参:[{}]",JSON.toJSONString(responseData),JSON.toJSONString(getFbcFeeRequest));
            if(responseData == null || responseData.getResCode()==null || !responseData.getResCode().equals(ErrorCode.SUCCESS.getCode())){
                GetReturnCostFailException getReturnCostFailException = new GetReturnCostFailException();
                throw getReturnCostFailException;
            }
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            t.setStatus(Transaction.SUCCESS);
        }catch (GetReturnCostFailException getReturnCostFailException){
            Cat.logError("Feign 获取取还车费用失败！", getReturnCostFailException);
            t.setStatus(getReturnCostFailException);
            throw getReturnCostFailException;
        }catch (Exception e){
            GetReturnCostErrorException getReturnCostErrorException = new GetReturnCostErrorException();
            Cat.logError("Feign 获取取还车费用接口异常",getReturnCostErrorException);
            t.setStatus(getReturnCostErrorException);
            throw getReturnCostErrorException;
        }
        List<PriceFbcFeeResponseDetail> fbcFeeResults = responseData.getData().getFbcFeeResults();

        getReturnResponse.setSumJudgeFreeFee(sumJudgeFreeFee);
        fbcFeeResults.forEach(fbcFeeResponse -> {
            if ("get".equalsIgnoreCase(fbcFeeResponse.getGetReturnType())) {
                int expectedShouldFee = Integer.valueOf(fbcFeeResponse.getExpectedShouldFee());
                RenterOrderCostDetailEntity renterOrderCostDetailEntity = new RenterOrderCostDetailEntity();
                renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
                renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.SRV_GET_COST.getTxt());
                renterOrderCostDetailEntity.setCount(1D);
                renterOrderCostDetailEntity.setTotalAmount(-expectedShouldFee);
                listCostDetail.add(renterOrderCostDetailEntity);

                RenterOrderSubsidyDetailDTO renterOrderSubsidy = new RenterOrderSubsidyDetailDTO();
                renterOrderSubsidy.setSubsidyTypeName(SubsidyTypeCodeEnum.GET_CAR.getDesc());
                renterOrderSubsidy.setSubsidyTypeCode(SubsidyTypeCodeEnum.GET_CAR.getCode());
                renterOrderSubsidy.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
                renterOrderSubsidy.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());
                renterOrderSubsidy.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
                renterOrderSubsidy.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
                renterOrderSubsidy.setSubsidyDesc("平台补贴给租客的取车费用！");
                int expectedRealFee = Integer.valueOf(fbcFeeResponse.getExpectedRealFee());

                renterOrderSubsidy.setSubsidyAmount(expectedShouldFee - expectedRealFee);
                renterOrderSubsidy.setSubsidyVoucher("");
                listCostSubsidy.add(renterOrderSubsidy);

                getReturnResponse.setGetFee(Integer.valueOf(fbcFeeResponse.getExpectedRealFee()));
                getReturnResponse.setGetShouldFee(Integer.valueOf(fbcFeeResponse.getExpectedShouldFee()));
                getReturnResponse.setGetInitFee(Integer.valueOf(fbcFeeResponse.getBaseFee()));
                getReturnResponse.setGetTimePeriodUpPrice(fbcFeeResponse.getTimePeriodUpPrice());
                getReturnResponse.setGetDistanceUpPrice(fbcFeeResponse.getDistanceUpPrice());
                getReturnResponse.setGetCicrleUpPrice(fbcFeeResponse.getCicrleUpPrice());
                getReturnResponse.setGetShowDistance(Double.valueOf(fbcFeeResponse.getShowDistance()));


            } else if ("return".equalsIgnoreCase(fbcFeeResponse.getGetReturnType())) {
                int expectedShouldFee = Integer.valueOf(fbcFeeResponse.getExpectedShouldFee());
                RenterOrderCostDetailEntity renterOrderCostDetailEntity = new RenterOrderCostDetailEntity();
                renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
                renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.SRV_RETURN_COST.getTxt());
                renterOrderCostDetailEntity.setCount(1D);
                renterOrderCostDetailEntity.setTotalAmount(-expectedShouldFee);
                listCostDetail.add(renterOrderCostDetailEntity);

                RenterOrderSubsidyDetailDTO renterOrderSubsidy = new RenterOrderSubsidyDetailDTO();
                renterOrderSubsidy.setSubsidyTypeName(SubsidyTypeCodeEnum.RETURN_CAR.getDesc());
                renterOrderSubsidy.setSubsidyTypeCode(SubsidyTypeCodeEnum.RETURN_CAR.getCode());
                renterOrderSubsidy.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
                renterOrderSubsidy.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());
                renterOrderSubsidy.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
                renterOrderSubsidy.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
                renterOrderSubsidy.setSubsidyDesc("平台补贴给租客的还车费用！");
                int expectedRealFee = Integer.valueOf(fbcFeeResponse.getExpectedRealFee());
                renterOrderSubsidy.setSubsidyAmount(expectedShouldFee - expectedRealFee);
                renterOrderSubsidy.setSubsidyVoucher("");
                listCostSubsidy.add(renterOrderSubsidy);

                getReturnResponse.setReturnFee(Integer.valueOf(fbcFeeResponse.getExpectedRealFee()));
                getReturnResponse.setReturnShouldFee(Integer.valueOf(fbcFeeResponse.getExpectedShouldFee()));
                getReturnResponse.setReturnInitFee(Integer.valueOf(fbcFeeResponse.getBaseFee()));
                getReturnResponse.setReturnTimePeriodUpPrice(fbcFeeResponse.getTimePeriodUpPrice());
                getReturnResponse.setReturnDistanceUpPrice(fbcFeeResponse.getDistanceUpPrice());
                getReturnResponse.setReturnCicrleUpPrice(fbcFeeResponse.getCicrleUpPrice());
                getReturnResponse.setReturnShowDistance(Double.valueOf(fbcFeeResponse.getShowDistance()));
            }
        });
        getReturnCostDto.setGetReturnResponseVO(getReturnResponse);
        getReturnCostDto.setRenterOrderCostDetailEntityList(listCostDetail);
        getReturnCostDto.setRenterOrderSubsidyDetailDTOList(listCostSubsidy);
        return getReturnCostDto;
    }

    /**
     *
     * 取还车订单类型
     * @param isPackageOrder
     * @return
     */
    private String getIsPackageOrder(Boolean isPackageOrder){
        if(isPackageOrder != null && isPackageOrder){
            // 套餐订单
            return "package";
        }else{
            // 普通订单
            return "general";
        }
    }
    /**
     * 根据来源判断渠道
     * @param source
     * @return
     */
    private ChannelNameTypeEnum getChannelCode(Integer source){
        if(null == source){
            return ChannelNameTypeEnum.APP;
        }

        //携程：400，同程：401，平安
        if(source.intValue() == 400 || source.intValue() == 401 || source.intValue() == 402){
            return ChannelNameTypeEnum.OTA;
        }

        return ChannelNameTypeEnum.APP;
    }

    /**
     * 计算取还车距离
     * @return
     */
    private static Float getRealDistance(String carLon,String carLat,String origionCarLon,String originCarLat){
        try {
            if(StringUtils.isBlank(carLon) || StringUtils.isBlank(carLat)
                    || StringUtils.isBlank(origionCarLon) || StringUtils.isBlank(originCarLat)) {

                return 0F;
            }
            return (float) calcDistance(Double.valueOf(carLon),Double.valueOf(carLat),
                    Double.valueOf(origionCarLon), Double.valueOf(originCarLat));
        } catch (Exception e) {
            log.error("getRealDistance计算取还车距离报错距离返回0：",e);
        }
        return 0F;
    }

    /**
     * 计算距离 (和数据库算法统一)
     * @param originCarLat
     * @param origionCarLon
     * @param carLat
     * @param carLon
     * @return
     */
    private static double calcDistance(double carLon,double carLat,double origionCarLon,double originCarLat){
        return new BigDecimal(
                6378.137*2*Math.asin(Math.sqrt(Math.pow(Math.sin( (originCarLat*Math.PI/180-carLat*Math.PI/180)/2),2)
                        +Math.cos(originCarLat*Math.PI/180)*Math.cos(carLat*Math.PI/180)*
                        Math.pow(Math.sin( (origionCarLon*Math.PI/180-carLon*Math.PI/180)/2),2))))
                .doubleValue();
    }


    /**
     * 根据entryCode判断渠道
     * @param entryCode
     * @return
     */
    private ChannelNameTypeEnum getChannelCodeByEntryCode(String entryCode){
        if(StringUtils.isEmpty(entryCode)){
            return ChannelNameTypeEnum.APP;
        }

        //ota平台  EX021只代表订单为套餐
        if(entryCode.equals("ota")){
            return ChannelNameTypeEnum.OTA;
        }
        //代步车渠道、安联
        else if(entryCode.equals("EX011") || entryCode.equals("EX022") ||
                entryCode.equals("EX030") || entryCode.equals("scooter")){
            return ChannelNameTypeEnum.SCOOTER;
        }
        //App
        else if(entryCode.equals("app")){
            return ChannelNameTypeEnum.APP;
        }

        return ChannelNameTypeEnum.APP;
    }


    /**
     * 获取取还车超运能信息
     * @param getReturnCarOverCostReqDto
     * @return GetReturnOverCostDTO
     */
    public GetReturnOverCostDTO getGetReturnOverCost(GetReturnCarOverCostReqDto getReturnCarOverCostReqDto) {
        GetReturnOverCostDTO getReturnOverCostDTO = new GetReturnOverCostDTO();
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = new ArrayList<>();

        LocalDateTime rentTime = getReturnCarOverCostReqDto.getCostBaseDTO().getStartTime();
        LocalDateTime revertTime = getReturnCarOverCostReqDto.getCostBaseDTO().getEndTime();
        Integer cityCode = getReturnCarOverCostReqDto.getCityCode();

        // 初始化数据
        GetReturnOverTransportDTO getReturnOverTransport = new GetReturnOverTransportDTO(false, 0, false, 0);
        getReturnOverTransport.setIsUpdateRentTime(true);
        getReturnOverTransport.setIsUpdateRevertTime(true);
        if (cityCode == null || (rentTime == null && revertTime == null)) {
            getReturnOverCostDTO.setRenterOrderCostDetailEntityList(renterOrderCostDetailEntityList);
            return getReturnOverCostDTO;
        }
        try {
            // 超运能后计算附加费的订单类型列表1-短租订单和3-平台套餐订单
            List<Integer> orderTypeList = Arrays.asList(ORDER_TYPES);
            // 超运能后计算附加费标志
            Boolean isAddFee = orderTypeList.contains(getReturnCarOverCostReqDto.getOrderType());
            //TODO apollo中获取配置参数
            Integer nightBegin = 0/*Integer.valueOf(apolloCostConfig.getNightBeginStr())*/;
            Integer nightEnd = 0/*Integer.valueOf(apolloCostConfig.getNightEndStr())*/;
            Integer overTransportFee = this.getGetReturnOverTransportFee(cityCode);
            String rentTimeLongStr = String.valueOf(LocalDateTimeUtils.localDateTimeToLong(rentTime));

            if (rentTime != null) {
                ResponseObject<Boolean> getFlgResponse = null;
                Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "取车是否超运能");
                try{
                    Cat.logEvent(CatConstants.FEIGN_METHOD,"GetBackCityLimitFeignApi.isCityServiceLimit");
                    Long rentTimeLong = Long.valueOf(rentTimeLongStr.substring(0, 12));
                    Cat.logEvent(CatConstants.FEIGN_PARAM,"cityCode="+cityCode+"&rentTimeLong="+rentTimeLong);
                    getFlgResponse = getBackCityLimitFeignApi.isCityServiceLimit(cityCode, rentTimeLong);
                    Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(getFlgResponse));
                    if(getFlgResponse == null || getFlgResponse.getResCode() == null || !ErrorCode.SUCCESS.getCode().equals(getFlgResponse.getResCode())){
                        GetCarOverCostFailException getCarOverCostFailException = new GetCarOverCostFailException();
                        log.error("取车超运能获取失败",getCarOverCostFailException);
                        throw getCarOverCostFailException;
                    }
                    t.setStatus(Transaction.SUCCESS);
                }catch (GetCarOverCostFailException oe){
                    Cat.logError("Feign 取车超运能获取失败",oe);
                    t.setStatus(oe);
                    throw oe;
                }catch (Exception e){
                    log.error("Feign 取车超运能接口异常",e);
                    Cat.logError("Feign 取车超运能接口异常",e);
                    t.setStatus(e);
                    throw e;
                }finally {
                    t.complete();
                }

                boolean getFlag = getFlgResponse.getData();
                if (getFlag) {
                    // 取还车超出运能附加金额
                    if (isAddFee) {
                        getReturnOverTransport.setGetOverTransportFee(overTransportFee);
                        if(DateUtils.isNight(rentTimeLongStr, nightBegin, nightEnd)) {
                            //夜间
                            getReturnOverTransport.setNightGetOverTransportFee(overTransportFee);
                        }
                        RenterOrderCostDetailEntity renterOrderCostDetailEntity = new RenterOrderCostDetailEntity();
                        renterOrderCostDetailEntity.setTotalAmount(overTransportFee);
                        renterOrderCostDetailEntity.setCount(1D);
                        renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo());
                        renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getTxt());
                        renterOrderCostDetailEntity.setUnitPrice(overTransportFee);
                        renterOrderCostDetailEntityList.add(renterOrderCostDetailEntity);
                    }
                    // 标记取车时间超出运能
                    getReturnOverTransport.setIsGetOverTransport(true);
                } else {
                    getReturnOverTransport.setGetOverTransportFee(0);
                    getReturnOverTransport.setIsGetOverTransport(false);
                }
            }
            if (revertTime != null) {
                String revertTimeLongStr = String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime));
                ResponseObject<Boolean>  returnFlgResponse = null;
                Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL,"还车是否超运能");
                try{
                    Cat.logEvent(CatConstants.FEIGN_METHOD,"GetBackCityLimitFeignApi.isCityServiceLimit");
                    long revertTimeLong = Long.valueOf(revertTimeLongStr.substring(0, 12));
                    Cat.logEvent(CatConstants.FEIGN_PARAM,"cityCode="+cityCode+"&revertTimeLong="+revertTimeLong);
                    returnFlgResponse = getBackCityLimitFeignApi.isCityServiceLimit(cityCode, revertTimeLong);
                    Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(returnFlgResponse));
                    if(returnFlgResponse == null || returnFlgResponse.getResCode() == null || !ErrorCode.SUCCESS.getCode().equals(returnFlgResponse.getResCode())){
                        throw new ReturnCarOverCostFailException();
                    }
                    t.setStatus(Transaction.SUCCESS);
                }catch (ReturnCarOverCostFailException oe){
                    Cat.logError("还车是否超运能获取失败",oe);
                    t.setStatus(oe);
                    throw oe;
                }catch (Exception e){
                    Cat.logError("还车是否超运能接口异常",e);
                    t.setStatus(e);
                    throw  e;
                }finally {
                    t.complete();
                }

                Boolean returnFlag = returnFlgResponse.getData();
                if (returnFlag) {
                    // 取还车超出运能附加金额
                    if (isAddFee) {
                        getReturnOverTransport.setReturnOverTransportFee(overTransportFee);
                        if(DateUtils.isNight(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime)), nightBegin, nightEnd)) {
                            //夜间
                            getReturnOverTransport.setNightReturnOverTransportFee(overTransportFee);;
                        }
                        RenterOrderCostDetailEntity renterOrderCostDetailEntity = new RenterOrderCostDetailEntity();
                        renterOrderCostDetailEntity.setTotalAmount(overTransportFee);
                        renterOrderCostDetailEntity.setCount(1D);
                        renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo());
                        renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getTxt());
                        renterOrderCostDetailEntity.setUnitPrice(overTransportFee);
                        renterOrderCostDetailEntityList.add(renterOrderCostDetailEntity);
                    }
                    // 标记还车时间超出运能
                    getReturnOverTransport.setIsReturnOverTransport(true);
                } else {
                    getReturnOverTransport.setIsReturnOverTransport(false);
                    getReturnOverTransport.setReturnOverTransportFee(0);
                }
            }

        } catch (Exception e) {
            log.error("获取取还车超运能信息出错：",e);
        }
        getReturnOverCostDTO.setGetReturnOverTransportDTO(getReturnOverTransport);
        getReturnOverCostDTO.setRenterOrderCostDetailEntityList(renterOrderCostDetailEntityList);
        return getReturnOverCostDTO;
    }


    /**
     * 	取还车超出运能附加金额
     * @param cityCode 城市编码
     * @return
     */
    @SuppressWarnings("unchecked")
    private Integer getGetReturnOverTransportFee(Integer cityCode) {
        String premiumAmt = null;
        //调用取还车服务接口获取城市对应的超能溢价金额
        GetFbcFeeConfigRequest reqParam  = new GetFbcFeeConfigRequest();
        reqParam.setCityId(String.valueOf(cityCode));
        reqParam.setRequestTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(LocalDateTime.now())));
        ResponseData<PriceCarHumanFeeRule> responseData = null;
        Transaction t = Cat.newTransaction(com.atzuche.order.commons.CatConstants.FEIGN_CALL, "取还车超出运能附加金额配置");
        try{
            log.info("Feign 获取取还车超出运能附加金额入参:[{}]",JSON.toJSONString(reqParam));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FetchBackCarFeeFeignService.getPriceCarHumanFeeRuleConfig");
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(reqParam));
            responseData = fetchBackCarFeeFeignService.getPriceCarHumanFeeRuleConfig(reqParam);
            log.info("Feign 获取取还车超出运能附加金额结果:[{}],获取取还车超出运能附加金额入参:[{}]",JSON.toJSONString(responseData),JSON.toJSONString(reqParam));
            if(responseData == null || responseData.getResCode()==null){
                GetReturnOverCostFailException fail = new GetReturnOverCostFailException();
                throw fail;
            }
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            t.setStatus(Transaction.SUCCESS);
        }catch (GetReturnCostFailException e){
            Cat.logError("Feign 获取取还车超出运能附加金额失败！", e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            GetReturnOverCostErrorException error = new GetReturnOverCostErrorException();
            Cat.logError("Feign 获取取还车超出运能附加金额接口异常",error);
            t.setStatus(error);
            throw error;
        }
        if(ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
            return responseData.getData().getHumanFee().intValue();
        }
        try {
            //TODO  apollo获取配置信息
           // return Integer.valueOf(apolloCostConfig.getGetReturnOverTransportFee());
        } catch (Exception e) {
            log.error("获取取还车超运能溢价默认值异常：", e);
        }
        return GlobalConstant.GET_RETURN_OVER_COST;
    }

}
