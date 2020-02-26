package com.autoyol.platformcost;

import com.atzuche.config.common.entity.DepositConfigEntity;
import com.atzuche.config.common.entity.IllegalDepositConfigEntity;
import com.atzuche.config.common.entity.InsuranceConfigEntity;
import com.atzuche.config.common.entity.OilAverageCostEntity;
import com.autoyol.platformcost.enums.ExceptionCodeEnum;
import com.autoyol.platformcost.exception.RenterFeeCostException;
import com.autoyol.platformcost.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RenterFeeCalculatorUtils {
	//private static Logger logger = LoggerFactory.getLogger(FeeCalculatorUtils.class);
	private static final int UNIT_INSUR_AMT_INIT = 35;
	
	private static final Integer SERVICE_CHARGE_FEE = 20;
	
	/** 车辆购置价 25W */
	private static final int CARPURCHASEPRICE_250000 = 250000;
	
	//1亿表示100万以上区间
	private static final int[] TOTAL_AMT_RANGE = {80000, 100000, 120000, 140000 ,160000, 180000, 200000, 250000,300000,400000,1000000,100000000};
	private static final int[] TOTAL_AMT_LEVEL = {0, 1000,  1200,  1500,   2000,   2500,  3000, 3500, 5000, 10000, 15000, 50000};
	private static final Integer CAREEPOSIT = 1; //租车押金
	private static final Integer CAREEPOSITDEFULT = 3; //默认租车押金
	private static final Integer INTERNAL_STAFF_FLAG = 1;//内部员工标志
	public static final Integer RENT_ADJUST_AMT_INNER = 300; //内部员工调价金额支付300元。  租车预授权=租金+保险+去送车+不计免赔+300
	public static final Map<Integer, Integer> ILLEGAL_DEPOSIT = new HashMap<Integer, Integer>() {
        private static final long serialVersionUID = 1L;
        {
            put(0, 2000);  //非内部员工。2000
            put(1, 1);  //内部员工，1
        }
    };
    
	/**
	 * 计算租金
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param configHours 配置小时数
	 * @param carPriceOfDayList 车辆日期价格列表
	 * @return FeeResult
	 */
	public static FeeResult calRentAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours, List<CarPriceOfDay> carPriceOfDayList) {
		if (rentTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		if (carPriceOfDayList == null || carPriceOfDayList.isEmpty()) {
			throw new RenterFeeCostException(ExceptionCodeEnum.CAR_DAY_PRICE_LIST_IS_EMPTY);
		}
		// 计算日均价
		Integer holidayAverage = getHolidayAverageRentAMT(rentTime, revertTime, carPriceOfDayList);
		if (holidayAverage == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.CAL_HOLIDAY_AVERAGE_PRICE_EXCEPTION);
		}
		// 计算总租期
		Double rentDays = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		if (rentDays == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.COUNT_RENT_DAY_EXCEPTION);
		}
		// 总租金
		Integer rentAmt = new BigDecimal(holidayAverage*rentDays).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		rentAmt = (rentAmt == null || rentAmt < 1) ? 1 : rentAmt;
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(rentAmt);
		feeResult.setUnitCount(rentDays);
		feeResult.setUnitPrice(holidayAverage);
		return feeResult;
	}
	
	
	/**
	 * 计算日均价
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param configHours 配置小时数
	 * @param carPriceOfDayList 车辆日期价格列表
	 * @return Integer
	 */
	public static Integer getHolidayAverageRentAMT(LocalDateTime rentTime, LocalDateTime revertTime, List<CarPriceOfDay> carPriceOfDayList) {
		if (rentTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		if (carPriceOfDayList == null || carPriceOfDayList.isEmpty()) {
			throw new RenterFeeCostException(ExceptionCodeEnum.CAR_DAY_PRICE_LIST_IS_EMPTY);
		}
		List<LocalDate> days = CommonUtils.getHolidayRentDays(rentTime, revertTime);
		float totalHours = CommonUtils.getTotalHoursByRentAveragePrice(rentTime, revertTime);
		if (days != null && !days.isEmpty()) {
			Map<LocalDate, Integer> dayPrices = carPriceOfDayList.stream()
					.collect(Collectors.toMap(CarPriceOfDay::getCurDate, CarPriceOfDay::getDayPrice));
			if (days.size() == 1) { // 就是当天的价格，平日价或节假日价格
				LocalDate date = days.get(0);
				int price = dayPrices.get(date);
				return price;
			} else { // 含2天的情况。
				float top = 0.00f;
				float middle = 0.00f;
				float foot = 0.00f;
				// 第一天
				float HFirst = 0.00f;
				// 最后一天
				float HLast = 0.00f;
				// 第一天
				LocalDate dateFirst = days.get(0);
				HFirst = CommonUtils.getHolidayTopHours(rentTime, dateFirst.toString());
				int priceFirst = dayPrices.get(dateFirst);
				// 计算公式
				top = priceFirst * (HFirst / totalHours);
				// 中间
				if (days.size() > 2) {
					for (int i = 1; i < days.size() - 1; i++) {
						LocalDate date = days.get(i);
						int price = dayPrices.get(date);
						// 计算公式
						middle = middle + price * (24 / totalHours); // 累加
					}
				}
				// 最后一天
				LocalDate dateLast = days.get(days.size() - 1);
				HLast = CommonUtils.getHolidayFootHours(revertTime, dateLast.toString());
				int priceLast = dayPrices.get(dateLast);
				// 计算公式
				foot = priceLast * (HLast / totalHours);
				// 调整为四舍五入取整 150828
				int averageAmt = new BigDecimal(top + middle + foot).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				return averageAmt < 1 ? 1 : averageAmt;
			}
		}
		return 1;
	}
	
	
	/**
	 * 平台手续费
	 */
	public static FeeResult calServiceChargeFee() {
		FeeResult feeResult = new FeeResult(SERVICE_CHARGE_FEE, 1.0, SERVICE_CHARGE_FEE);
		return feeResult;
	}
	
	
	/**
	 * 计算总的全面保障费
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param getCarBeforeTime 提前时间（分钟数）
	 * @param returnCarAfterTime 延后时间（分钟数）
	 * @param configHours 配置小时数
	 * @param guidPrice 车辆指导价
	 * @param coefficient 会员系数
	 * @param easyCoefficient 车辆标签系数
	 * @return List<FeeResult>
	 */
	public static List<FeeResult> calcAbatementAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer getCarBeforeTime,Integer returnCarAfterTime, Integer configHours, Integer guidPrice, Double coefficient, Double easyCoefficient) {
		if (rentTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		coefficient = coefficient == null ? 1.0:coefficient;
		easyCoefficient = easyCoefficient == null ? 1.0:easyCoefficient;
		// 计算提前延后时间
		rentTime = CommonUtils.calBeforeTime(rentTime, getCarBeforeTime);
		revertTime = CommonUtils.calAfterTime(revertTime, returnCarAfterTime);
		// 计算租期
		Double abatementDay = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		if (abatementDay == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.COUNT_RENT_DAY_EXCEPTION);
		}
		if (guidPrice == null) {
			guidPrice = CARPURCHASEPRICE_250000;
		}
		int purchasePrice = guidPrice.intValue();
		AbatementConfig abatementConfig = null;
		for (AbatementConfig ac:CommonUtils.ABATEMENTCONFIG_List) {
			if (ac.getGuidPriceBegin() < purchasePrice && purchasePrice <= ac.getGuidPriceEnd()) {
				abatementConfig = ac;
				break;
			}
		}
		if (abatementDay.doubleValue() <= 7) {
			return getFeeResultList(abatementConfig, abatementDay, coefficient, easyCoefficient, 1);
		} else if (7 < abatementDay.doubleValue() && abatementDay.doubleValue() <= 15) {
			return getFeeResultList(abatementConfig, abatementDay, coefficient, easyCoefficient, 2);
		} else if (15 < abatementDay.doubleValue() && abatementDay.doubleValue() <= 25) {
			return getFeeResultList(abatementConfig, abatementDay, coefficient, easyCoefficient, 3);
		} else {
			return getFeeResultList(abatementConfig, abatementDay, coefficient, easyCoefficient, 4);
		}
	}
	
	
	/**
	 * 获取全面保障费阶梯价格
	 * @param abatementConfig 价格配置
	 * @param abatementDay 租期
	 * @param coefficient 会员系数
	 * @param easyCoefficient 车辆标签系数
	 * @param size 大小
	 * @return List<FeeResult>
	 */
	public static List<FeeResult> getFeeResultList(AbatementConfig abatementConfig, Double abatementDay, Double coefficient, Double easyCoefficient, Integer size) {
		List<FeeResult> feeResultList = new ArrayList<FeeResult>();
		for (int i=0; i<size; i++) {
			Integer curPrice = null;
			Double curDays = null;
			if (i == 0) {
				curPrice = abatementConfig.getAbatementUnitPrice7();
				curDays = abatementDay <= 7 ? abatementDay:7.0;
			} else if (i == 1) {
				curPrice = abatementConfig.getAbatementUnitPrice15();
				curDays = abatementDay <= 15 ? abatementDay - 7:8.0;
			} else if (i == 2) {
				curPrice = abatementConfig.getAbatementUnitPrice25();
				curDays = abatementDay <= 25 ? abatementDay - 15:10.0;
			} else {
				curPrice = abatementConfig.getAbatementUnitPriceOther();
				curDays = abatementDay - 25;
			}
			Integer unitPrice = (int) Math.ceil(curPrice*coefficient*easyCoefficient);
			Double unitCount = curDays;
			Integer totalFee = (int) Math.ceil(unitPrice*unitCount);
			FeeResult feeResult = new FeeResult(unitPrice, unitCount, totalFee);
			feeResultList.add(feeResult);
		}
		return feeResultList;
	}
	
	
	/**
	 * 计算平台保障费
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param getCarBeforeTime 提前时间（分钟数）
	 * @param returnCarAfterTime 延后时间（分钟数）
	 * @param configHours 配置小时数
	 * @param guidPrice 车辆指导价
	 * @param coefficient 会员系数
	 * @param easyCoefficient 车辆标签系数
	 * @param insuranceConfigs 平台保障费配置列表
	 * @return FeeResult
	 */
	public static FeeResult calInsurAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer getCarBeforeTime,Integer returnCarAfterTime, Integer configHours, Integer guidPrice, Double coefficient, Double easyCoefficient, List<InsuranceConfigEntity> insuranceConfigs) {
		if (rentTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		if (insuranceConfigs == null || insuranceConfigs.isEmpty()) {
			throw new RenterFeeCostException(ExceptionCodeEnum.INSURE_CONFIG_LIST_IS_EMPTY);
		}
		coefficient = coefficient == null ? 1.0:coefficient;
		easyCoefficient = easyCoefficient == null ? 1.0:easyCoefficient;
		// 计算提前延后时间
		rentTime = CommonUtils.calBeforeTime(rentTime, getCarBeforeTime);
		revertTime = CommonUtils.calAfterTime(revertTime, returnCarAfterTime);
		// 计算租期
		Double insurDays = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		if (insurDays == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.COUNT_RENT_DAY_EXCEPTION);
		}
		// 计算单价
		Integer unitInsurance = getUnitInsurance(guidPrice, insuranceConfigs);
		if (unitInsurance == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.INSURE_UNIT_PRICE_EXCEPTION);
		}
		// 经过系数后的单价
		unitInsurance = (int) Math.ceil(unitInsurance*coefficient*easyCoefficient);
		// 总价
		Integer totalInsurAmt = (int) Math.ceil(unitInsurance*insurDays);
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(totalInsurAmt);
		feeResult.setUnitCount(insurDays);
		feeResult.setUnitPrice(unitInsurance);
		return feeResult;
	}
	
	
	/**
	 * 根据车辆的购置价和购买年月(计算出折旧后的价格)，获得保费（单价）/天(普通订单)
	 * @param purchasePrice 车辆指导价
	 * @param insuranceConfigs 平台保障费配置列表
	 * @return Integer
	 */
	public static Integer getUnitInsurance(Integer purchasePrice, List<InsuranceConfigEntity> insuranceConfigs) {
		Integer unitInsurAmt = UNIT_INSUR_AMT_INIT;
		if (purchasePrice == null) {
			purchasePrice = CARPURCHASEPRICE_250000;
		}
		if (insuranceConfigs == null || insuranceConfigs.isEmpty()) {
			return unitInsurAmt;
		}
		for(InsuranceConfigEntity config:insuranceConfigs){
			int minPrice = config.getGuidPriceBegin()==null?0:config.getGuidPriceBegin();
			int maxPrice = config.getGuidPriceEnd();
			if (purchasePrice.intValue() == 0 && purchasePrice.intValue() == minPrice) {
				unitInsurAmt =  config.getInsuranceValue();
				break;
			}
			if(purchasePrice.intValue() > minPrice && purchasePrice.intValue() <= maxPrice) {
				unitInsurAmt =  config.getInsuranceValue();
				break;
			}
			if (maxPrice == -1) { //无穷大
				if (purchasePrice.intValue() > minPrice) {
					unitInsurAmt =  config.getInsuranceValue();
					break;
				}
			}
		} 
		return unitInsurAmt; 
	}
	
	
	/**
	 * 计算附加驾驶人保障费用
	 * @param unitExtraDriverInsure 附加险单价
	 * @param extraDriverCount 附加驾驶人数
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return FeeResult
	 */
	public static FeeResult calExtraDriverInsureAmt(Integer unitExtraDriverInsure,Integer extraDriverCount, LocalDateTime rentTime, LocalDateTime revertTime) {
		if (rentTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		unitExtraDriverInsure = unitExtraDriverInsure == null ? 20:unitExtraDriverInsure;
		extraDriverCount = extraDriverCount == null ? 0:extraDriverCount;
		long totalDays = CommonUtils.getDaysUpCeil(rentTime, revertTime);
		Integer totalFee = (int) (unitExtraDriverInsure*totalDays*extraDriverCount);
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(totalFee);
		feeResult.setUnitCount((double)totalDays*extraDriverCount);
		feeResult.setUnitPrice(unitExtraDriverInsure);
		return feeResult;
	}
	
	/**
	 * 计算车辆押金
	 * @param InternalStaff 是否内部员工（1是，其他否）
	 * @param cityCode 城市编号
	 * @param guidPrice 车辆残值
	 * @param carBrandTypeRadio 车型品牌系数
	 * @param carYearRadio 新车押金系数
	 * @param depositList 押金配置列表
	 * @param reliefPercetage 减免比例
	 * @return CarDepositAmtVO
	 */
	public static CarDepositAmtVO calCarDepositAmt(Integer cityCode, Integer guidPrice, Double carBrandTypeRadio, Double carYearRadio, List<DepositConfigEntity> depositList) {
        if (guidPrice == null) {
            throw new RenterFeeCostException(ExceptionCodeEnum.GUID_PRICE_IS_NULL);
        }
        //初始化车辆押金
        Integer suggestTotal = getSuggestTotalAmt(guidPrice);
        Boolean carbool = true;
        Integer cityCodeStr = cityCode == null ? null:Integer.valueOf(cityCode);
        if(null!=depositList&&depositList.size()>0){
            for (DepositConfigEntity dt : depositList) {
                if(CAREEPOSIT.equals(dt.getDepositType())
                        &&cityCodeStr.equals(dt.getCityCode())
                        &&guidPrice>Integer.valueOf(dt.getPurchasePriceBegin())
                        &&guidPrice<=Integer.valueOf(dt.getPurchasePriceEnd())){
                    suggestTotal = Integer.valueOf(dt.getDepositValue());
                    carbool = false;
                    break;
                }
                //处理购置价为0的情况
                if (CAREEPOSIT.equals(dt.getDepositType())
                        && cityCodeStr.equals(dt.getCityCode())
                        && guidPrice==0 && guidPrice == Integer.valueOf(dt.getPurchasePriceBegin())) {
                    suggestTotal = Integer.valueOf(dt.getDepositValue());
                    carbool = false;
                    break;
                }
            }

            //此处代表默认设置
            if(carbool){
                for (DepositConfigEntity dt : depositList) {
                    if(CAREEPOSITDEFULT.equals(dt.getDepositType())
                            && guidPrice>Integer.valueOf(dt.getPurchasePriceBegin())
                            && guidPrice<=Integer.valueOf(dt.getPurchasePriceEnd())){
                        suggestTotal = Integer.valueOf(dt.getDepositValue());
                        break;
                    }
                    //处理购置价为0的情况
                    if (CAREEPOSITDEFULT.equals(dt.getDepositType())
                            && guidPrice == 0
                            && guidPrice == Integer.valueOf(dt.getPurchasePriceBegin())) {
                        suggestTotal = Integer.valueOf(dt.getDepositValue());
                        break;
                    }
                }
            }
        }
        double coefficient = 1.0;
        if((carBrandTypeRadio != null && carBrandTypeRadio != 0) || (carYearRadio != null && carYearRadio > 1)) {
            coefficient = 1.3;
        }
        double carDepositAmt = 0.0;
        if(guidPrice > 1500000) {
            coefficient = 1.0;
            carDepositAmt = suggestTotal;
        } else {
            carDepositAmt = suggestTotal * coefficient;
        }
        CarDepositAmtVO carDepositAmtVO = new CarDepositAmtVO();
        carDepositAmtVO.setCarSpecialCoefficient(carBrandTypeRadio);
        carDepositAmtVO.setNewCarCoefficient(carYearRadio);
        carDepositAmtVO.setCarDepositAmt((int) carDepositAmt);
        carDepositAmtVO.setSuggestTotal((int)suggestTotal);
        carDepositAmtVO.setCarDepositRadio(coefficient);

        return carDepositAmtVO;
	}
	

	/**
	 * 根据车辆的购买价格计算建议的租车押金（预授权额度）
	 * @param purchasePrice 车辆残值
	 * @return Integer
	 */
	public static Integer getSuggestTotalAmt(Integer purchasePrice){
		int minPrice = 0;
		int maxPrice = 0;
		for (int i = 0; i < TOTAL_AMT_RANGE.length; i++) {
			maxPrice = TOTAL_AMT_RANGE[i];
			if(purchasePrice > minPrice && purchasePrice <= maxPrice){
				return TOTAL_AMT_LEVEL[i];
			}
			minPrice = maxPrice;
		}
		return TOTAL_AMT_LEVEL[TOTAL_AMT_LEVEL.length-1];
	}
	
	
	/**
	 * 计算违章押金
	 * @param internalStaff 是否内部员工（1是，其他否）
	 * @param cityCode 城市编号
	 * @param carPlateNum 车牌号
	 * @param specialCityCodes 特殊城市（逗号分隔的城市编码）
	 * @param specialIllegalDepositAmt 特殊车牌合特殊城市对应的特殊押金值
	 * @param illegalDepositList 违章押金配置
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return Integer
	 */
	public static Integer calIllegalDepositAmt(Integer cityCode, String carPlateNum, String specialCityCodes, Integer specialIllegalDepositAmt,
                                               List<IllegalDepositConfigEntity> illegalDepositList, LocalDateTime rentTime, LocalDateTime revertTime) {
		Integer illegalDepositAmt = ILLEGAL_DEPOSIT.get(0);
		if (carPlateNum != null && !"".equals(carPlateNum) && specialCityCodes != null && !"".equals(specialCityCodes)) {
			if("粤".equals(carPlateNum.substring(0,1)) && cityCode != null && specialCityCodes.contains(String.valueOf(cityCode))){
				illegalDepositAmt = specialIllegalDepositAmt == null ? illegalDepositAmt:specialIllegalDepositAmt;
				return illegalDepositAmt;
	        }
		}
		illegalDepositAmt = getIllegalDepositAmt(cityCode, illegalDepositList, rentTime, revertTime);
		return illegalDepositAmt;
	}
	
	
	/**
	 * 通过配置获取对应的违章押金
	 * @param cityCode 城市编号
	 * @param illegalDepositList 违章押金配置列表
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return Integer
	 */
	public static Integer getIllegalDepositAmt(Integer cityCode, List<IllegalDepositConfigEntity> illegalDepositList, LocalDateTime rentTime, LocalDateTime revertTime) {
		Integer illegalDepositAmt = ILLEGAL_DEPOSIT.get(0);
		if (cityCode == null) {
			return illegalDepositAmt;
		}
		if (illegalDepositList == null || illegalDepositList.isEmpty()) {
			return illegalDepositAmt;
		}
		if (rentTime == null || revertTime == null) {
			return illegalDepositAmt;
		}
		// 计算天数
		long days = CommonUtils.getDaysUpCeil(rentTime, revertTime);
		for (IllegalDepositConfigEntity idc:illegalDepositList) {
			if (cityCode.equals(idc.getCityCode()) && 
					idc.getLeaseMin() != null && 
					idc.getLeaseMax() != null && 
					days >= idc.getLeaseMin() &&
					days <= idc.getLeaseMax() && 
					idc.getDepositAmt() != null) {
				illegalDepositAmt = idc.getDepositAmt();
				break;
			}
		}
		return illegalDepositAmt;
	}
	
	/**
	 * 计算超里程费用
	 * @param dayMileage 日均限里程数
	 * @param guideDayPrice 车辆日租金指导价
	 * @param getmileage 取车里程数
	 * @param returnMileage 还车里程数
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param configHours 配置小时数
	 * @return Integer
	 */
	public static Integer calMileageAmt(Integer dayMileage, Integer guideDayPrice, Integer getmileage, Integer returnMileage, LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours) {
		if (rentTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new RenterFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		if (getmileage == null || returnMileage == null || (getmileage >= returnMileage)) {
			return 0;
		}
		if (dayMileage == null || dayMileage == 0|| guideDayPrice == null) {
			return 0;
		}
		// 计算租期
		Double rentDays = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		// 超里程数
		Double distance=CommonUtils.sub((returnMileage-getmileage), CommonUtils.mul(rentDays, dayMileage));
		if (distance == null || distance <= 0) {
			return 0;
		}
		double radio=CommonUtils.mul(CommonUtils.D_Mileage, guideDayPrice);  //0.005;//超日限里程系数 * 日均价
		if (radio > CommonUtils.MAX_Mileage) {	//5;//超日限里程最大每天
			radio = CommonUtils.MAX_Mileage;
		} else if (radio < CommonUtils.MIN_Mileage) {   //0.5;//超日限里最小每天
			radio = CommonUtils.MIN_Mileage;
		}
		return (int) Math.ceil(CommonUtils.mul(distance, radio));  // 超出距离 * 系数
	}
	
	
	/**
	 * 计算油费
	 * @param cityCode 城市编号
	 * @param oilVolume 油箱容量
	 * @param engineType 油品类型
	 * @param getOilScale 取车油表刻度
	 * @param returnOilScale 还车油表刻度
	 * @param oilAverageList 油费单价配置列表
	 * @param oilScaleDenominator 总油表刻度
	 * @return Integer
	 */
	public static Integer calOilAmt(Integer cityCode, Integer oilVolume, Integer engineType, Integer getOilScale, Integer returnOilScale, List<OilAverageCostEntity> oilAverageList, Integer oilScaleDenominator) {
		//动力类型，1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5
		if (engineType==null||oilVolume==null||oilVolume==0||getOilScale==null||returnOilScale==null) {
			return 0;
		}
		if (oilScaleDenominator == null || oilScaleDenominator == 0) {
			return 0;
		}
		Integer oilVolumeChange = getOilScale - returnOilScale;
		// 获取单价
		Double averageCost = CommonUtils.getAverageCost(engineType, cityCode, oilAverageList); 
		Integer oilCost = 0;
		Double oilCostDou = CommonUtils.div((CommonUtils.mul(oilVolume*oilVolumeChange, averageCost)), oilScaleDenominator);
		oilCostDou = oilCostDou == null ? 0.0:oilCostDou;
		if (oilCostDou < 0) {
			oilCost = (int) Math.ceil(oilCostDou);
		} else {
			oilCost=(int) Math.floor(oilCostDou);
		}
		return oilCost;
	}
	
	
}
