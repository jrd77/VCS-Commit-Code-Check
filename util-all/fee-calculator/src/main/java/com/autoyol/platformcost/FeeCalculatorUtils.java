package com.autoyol.platformcost;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.FeeResult;
import com.autoyol.platformcost.model.InsuranceConfig;

public class FeeCalculatorUtils {
	//private static Logger logger = LoggerFactory.getLogger(FeeCalculatorUtils.class);
	private static final int UNIT_INSUR_AMT_INIT = 35;
	
	private static final double COEFFICIENT_INIT = 1.0;
	private static final double COEFFICIENT_NOVICE = 1.2;
	
	/** 车辆购置价 25W */
	private static final int CARPURCHASEPRICE_250000 = 250000;
	/** 车辆购置价 40W */
	private static final int CARPURCHASEPRICE_400000 = 400000;
	/** 车辆购置价 60W */
	private static final int CARPURCHASEPRICE_600000 = 600000;
	/** 车辆购置价 80W */
	private static final int CARPURCHASEPRICE_800000 = 800000;
	/** 车辆购置价 100W */
	private static final int CARPURCHASEPRICE_1000000 = 1000000;
	/** 车辆购置价 150W */
	private static final int CARPURCHASEPRICE_1500000 = 1500000;
	/** 车辆购置价 200W */
	private static final int CARPURCHASEPRICE_2000000 = 2000000;
	/** 车辆购置价 250W */
	private static final int CARPURCHASEPRICE_2500000 = 2500000;
	/** 车辆购置价 300W */
	private static final int CARPURCHASEPRICE_3000000 = 3000000;
	/** 车辆购置价 350W */
	private static final int CARPURCHASEPRICE_3500000 = 3500000;
	/** 车辆购置价 400W */
	private static final int CARPURCHASEPRICE_4000000 = 4000000;
	/** 车辆购置价 450W */
	private static final int CARPURCHASEPRICE_4500000 = 4500000;
	
	
	/**
	 * 计算租金
	 * @param rentTime
	 * @param revertTime
	 * @param configHours
	 * @param carPriceOfDayList
	 * @return
	 */
	public static FeeResult calRentAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours, List<CarPriceOfDay> carPriceOfDayList) {
		// 计算日均价
		Integer holidayAverage = getHolidayAverageRentAMT(rentTime, revertTime, carPriceOfDayList);
		// 计算总租期
		Double rentDays = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		// 总租金
		Integer rentAmt = new BigDecimal(holidayAverage*rentDays).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		rentAmt = rentAmt < 1 ? 1 : rentAmt;
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(rentAmt);
		feeResult.setUnitCount(rentDays);
		feeResult.setUnitPrice(holidayAverage);
		return feeResult;
	}
	
	
	/**
	 * 计算日均价
	 * @param rentTime
	 * @param revertTime
	 * @param configHours
	 * @param carPriceOfDayList
	 * @return
	 */
	public static Integer getHolidayAverageRentAMT(LocalDateTime rentTime, LocalDateTime revertTime, List<CarPriceOfDay> carPriceOfDayList) {
		List<String> days = CommonUtils.getHolidayRentDays(rentTime, revertTime);
		float totalHours = CommonUtils.getTotalHoursByRentAveragePrice(rentTime, revertTime);
		if (days != null && !days.isEmpty()) {
			Map<String, Integer> dayPrices = carPriceOfDayList.stream()
					.collect(Collectors.toMap(CarPriceOfDay::getDateStr, CarPriceOfDay::getDayPrice));
			if (days.size() == 1) { // 就是当天的价格，平日价或节假日价格
				String date = days.get(0).replaceAll("-", "");
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
				String dateFirst = days.get(0);
				HFirst = CommonUtils.getHolidayTopHours(rentTime, dateFirst);
				int priceFirst = dayPrices.get(dateFirst.replaceAll("-", ""));
				// 计算公式
				top = priceFirst * (HFirst / totalHours);
				// 中间
				if (days.size() > 2) {
					for (int i = 1; i < days.size() - 1; i++) {
						String date = days.get(i).replaceAll("-", "");
						int price = dayPrices.get(date);
						// 计算公式
						middle = middle + price * (24 / totalHours); // 累加
					}
				}
				// 最后一天
				String dateLast = days.get(days.size() - 1);
				HLast = CommonUtils.getHolidayFootHours(revertTime, dateLast);
				int priceLast = dayPrices.get(dateLast.replaceAll("-", ""));
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
	 * 计算总的全面保障费
	 * @param rentTime
	 * @param revertTime
	 * @param getCarBeforeTime
	 * @param returnCarAfterTime
	 * @param configHours
	 * @param guidPrice
	 * @param coefficient
	 * @param easyCoefficient
	 * @return
	 */
	public static Integer calcAbatementAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer getCarBeforeTime,Integer returnCarAfterTime, Integer configHours, Integer guidPrice, Double coefficient, Double easyCoefficient) {
		// 计算提前延后时间
		rentTime = CommonUtils.calBeforeTime(rentTime, getCarBeforeTime);
		revertTime = CommonUtils.calAfterTime(revertTime, returnCarAfterTime);
		// 计算租期
		Double abatementDay = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		if (guidPrice == null) {
			guidPrice = CARPURCHASEPRICE_250000;
			//logger.error("计算总的全面保障费车辆指导价为空，取默认指导价；[{}]", CARPURCHASEPRICE_250000);
			//Cat.logError("计算总的全面保障费车辆指导价为空，取默认指导价；"+CARPURCHASEPRICE_250000,new PlatformCostException(ErrorCode.GUID_PRICE_IS_NULL));
			//throw new PlatformCostException(ErrorCode.GUID_PRICE_IS_NULL);
		}
		int purchasePrice = guidPrice.intValue();
		Integer result = null;
		if (abatementDay.doubleValue() <= 7) {
			if (purchasePrice <= CARPURCHASEPRICE_250000) {
				result = (int) Math.ceil(40 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_250000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_400000) {
				result = (int) Math.ceil(50 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_400000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_600000) {
				result = (int) Math.ceil(90 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_600000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_800000) {
				result = (int) Math.ceil(100 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_800000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1000000) {
				result = (int) Math.ceil(120 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_1000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1500000) {
				result = (int) Math.ceil(225 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_1500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2000000) {
				result = (int) Math.ceil(300 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_2000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2500000) {
				result = (int) Math.ceil(375 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_2500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3000000) {
				result = (int) Math.ceil(450 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_3000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3500000) {
				result = (int) Math.ceil(525 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_3500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4000000) {
				result = (int) Math.ceil(600 * abatementDay.doubleValue());
			} else if(CARPURCHASEPRICE_4000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4500000) {
				result = (int) Math.ceil(675 * abatementDay.doubleValue());
			} else {
				// 150万≤车辆购置价
				result = (int) Math.ceil(750 * abatementDay.doubleValue());
			}

		// 7天<租期<=15天的部分
		} else if (7 < abatementDay.doubleValue() 
				&& abatementDay.doubleValue() <= 15) {
			if (purchasePrice <= CARPURCHASEPRICE_250000) {
				result = (int) Math.ceil(40 * 7 + 30 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_250000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_400000) {
				result = (int) Math.ceil(50 * 7 + 40 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_400000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_600000) {
				result = (int) Math.ceil(90 * 7 + 70 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_600000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_800000) {
				result = (int) Math.ceil(100 * 7 + 80 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_800000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1000000) {
				result = (int) Math.ceil(120 * 7 + 100 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_1000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1500000) {
				result = (int) Math.ceil(225 * 7 + 180 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_1500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2000000) {
				result = (int) Math.ceil(300 * 7 + 240 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_2000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2500000) {
				result = (int) Math.ceil(375 * 7 + 300 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_2500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3000000) {
				result = (int) Math.ceil(450 * 7 + 360 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_3000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3500000) {
				result = (int) Math.ceil(525 * 7 + 420 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_3500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4000000) {
				result = (int) Math.ceil(600 * 7 + 480 * (abatementDay.doubleValue() - 7));
			} else if(CARPURCHASEPRICE_4000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4500000) {
				result = (int) Math.ceil(675 * 7 + 540 * (abatementDay.doubleValue() - 7));
			} else {
				// 150万≤车辆购置价
				result = (int) Math.ceil(750 * 7 + 600 * (abatementDay.doubleValue() - 7));
			}
		// 15<租期<=25天的部分
		} else if (15 < abatementDay.doubleValue() 
				&& abatementDay.doubleValue() <= 25) {
			if (purchasePrice <= CARPURCHASEPRICE_250000) {
				result = (int) Math.ceil((40 * 7) + (30 * 8) + (25 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_250000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_400000) {
				result = (int) Math.ceil((50 * 7) + (40 * 8) + (35 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_400000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_600000) {
				result = (int) Math.ceil((90 * 7) + (70 * 8) + (60 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_600000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_800000) {
				result = (int) Math.ceil((100 * 7) + (80 * 8) + (70 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_800000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1000000) {
				result = (int) Math.ceil((120 * 7) + (100 * 8) + (80 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_1000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1500000) {
				result = (int) Math.ceil((225 * 7) + (180 * 8) + (144 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_1500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2000000) {
				result = (int) Math.ceil((300 * 7) + (240 * 8) + (192 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_2000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2500000) {
				result = (int) Math.ceil((375 * 7) + (300 * 8) + (240 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_2500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3000000) {
				result = (int) Math.ceil((450 * 7) + (360 * 8) + (288 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_3000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3500000) {
				result = (int) Math.ceil((525 * 7) + (420 * 8) + (336 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_3500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4000000) {
				result = (int) Math.ceil((600 * 7) + (480 * 8) + (384 * (abatementDay.doubleValue() - 15)));
			} else if(CARPURCHASEPRICE_4000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4500000) {
				result = (int) Math.ceil((675 * 7) + (540 * 8) + (432 * (abatementDay.doubleValue() - 15)));
			} else {
				// 150万≤车辆购置价
				result = (int) Math.ceil((750 * 7) + (600 * 8) + (480 * (abatementDay.doubleValue() - 15)));
			}
			
			
		// 租期>25天部分
		} else {
			if (purchasePrice <= CARPURCHASEPRICE_250000) {
				result = (int) Math.ceil((40 * 7) + (30 * 8) + (25 * 10) + (20 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_250000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_400000) {
				result = (int) Math.ceil((50 * 7) + (40 * 8) + (35 * 10) + (30 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_400000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_600000) {
				result = (int) Math.ceil((90 * 7) + (70 * 8) + (60 * 10) + (50 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_600000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_800000) {
				result = (int) Math.ceil((100 * 7) + (80 * 8) + (70 * 10) + (60 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_800000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1000000) {
				result = (int) Math.ceil((120 * 7) + (100 * 8) + (80 * 10) + (70 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_1000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_1500000) {
				result = (int) Math.ceil((225 * 7) + (180 * 8) + (144 * 10) + (115 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_1500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2000000) {
				result = (int) Math.ceil((300 * 7) + (240 * 8) + (192 * 10) + (154 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_2000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_2500000) {
				result = (int) Math.ceil((375 * 7) + (300 * 8) + (240 * 10) + (192 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_2500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3000000) {
				result = (int) Math.ceil((450 * 7) + (360 * 8) + (288 * 10) + (230 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_3000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_3500000) {
				result = (int) Math.ceil((525 * 7) + (420 * 8) + (336 * 10) + (269 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_3500000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4000000) {
				result = (int) Math.ceil((600 * 7) + (480 * 8) + (384 * 10) + (307 * (abatementDay.doubleValue() - 25)));
			} else if(CARPURCHASEPRICE_4000000 < purchasePrice && purchasePrice <= CARPURCHASEPRICE_4500000) {
				result = (int) Math.ceil((675 * 7) + (540 * 8) + (432 * 10) + (346 * (abatementDay.doubleValue() - 25)));
			} else {
				// 150万≤车辆购置价
				result = (int) Math.ceil((750 * 7) + (600 * 8) + (480 * 10) + (384 * (abatementDay.doubleValue() - 25)));
			}
		}
		return (int) Math.ceil(result*coefficient*easyCoefficient);
	}
	
	
	/**
	 * 计算平台保障费
	 * @param rentTime
	 * @param revertTime
	 * @param getCarBeforeTime
	 * @param returnCarAfterTime
	 * @param configHours
	 * @param guidPrice
	 * @param coefficient
	 * @param easyCoefficient
	 * @param insuranceConfigs
	 * @return
	 */
	public static FeeResult calInsurAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer getCarBeforeTime,Integer returnCarAfterTime, Integer configHours, Integer guidPrice, Double coefficient, Double easyCoefficient, List<InsuranceConfig> insuranceConfigs) {
		// 计算提前延后时间
		rentTime = CommonUtils.calBeforeTime(rentTime, getCarBeforeTime);
		revertTime = CommonUtils.calAfterTime(revertTime, returnCarAfterTime);
		// 计算租期
		Double insurDays = CommonUtils.getRentDays(rentTime, revertTime, configHours);
		// 计算单价
		Integer unitInsurance = getUnitInsurance(guidPrice, insuranceConfigs);
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
	 * @param purchasePrice
	 * @param boughtYear
	 * @param boughtMonth
	 * @param insuranceConfigs
	 * @return
	 */
	public static Integer getUnitInsurance(Integer purchasePrice, List<InsuranceConfig> insuranceConfigs) {
		Integer unitInsurAmt = UNIT_INSUR_AMT_INIT;
		if (purchasePrice == null) {
			purchasePrice = CARPURCHASEPRICE_250000;
			//logger.error("计算平台保障费车辆指导价为空，取默认指导价；[{}]", CARPURCHASEPRICE_250000);
			//Cat.logError("计算平台保障费车辆指导价为空，取默认指导价；"+CARPURCHASEPRICE_250000,new PlatformCostException(ErrorCode.GUID_PRICE_IS_NULL));
			//throw new PlatformCostException(ErrorCode.GUID_PRICE_IS_NULL);
		}
		if (insuranceConfigs == null || insuranceConfigs.isEmpty()) {
			//logger.error("计算平台保障费insuranceConfigs配置为空，保险单价默认取；", UNIT_INSUR_AMT_INIT);
			//Cat.logError("计算平台保障费insuranceConfigs配置为空，保险单价默认取；"+UNIT_INSUR_AMT_INIT,new PlatformCostException(ErrorCode.INSUR_CONFIG_NOT_EXIT));
			return unitInsurAmt;
			//throw new PlatformCostException(ErrorCode.INSUR_CONFIG_NOT_EXIT);
		}
		for(InsuranceConfig config:insuranceConfigs){
			int minPrice = config.getGuidPriceBegin();
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
	 * @param unitExtraDriverInsure
	 * @param extraDriverCount
	 * @param rentTime
	 * @param revertTime
	 * @return
	 */
	public static FeeResult calExtraDriverInsureAmt(Integer unitExtraDriverInsure,Integer extraDriverCount, LocalDateTime rentTime, LocalDateTime revertTime) {
		long totalDays = CommonUtils.getDaysUpCeil(rentTime, revertTime);
		Integer totalFee = (int) (unitExtraDriverInsure*totalDays*extraDriverCount);
		FeeResult feeResult = new FeeResult();
		feeResult.setTotalFee(totalFee);
		feeResult.setUnitCount((double)totalDays);
		feeResult.setUnitPrice(unitExtraDriverInsure);
		return feeResult;
	}
}
