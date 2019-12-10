package com.autoyol.platformcost;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.autoyol.platformcost.model.CarDepositAmtVO;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.DepositText;
import com.autoyol.platformcost.model.FeeResult;
import com.autoyol.platformcost.model.IllegalDepositConfig;
import com.autoyol.platformcost.model.InsuranceConfig;
import com.autoyol.platformcost.model.OilAverageCostBO;

public class RenterFeeCalculatorUtils {
	//private static Logger logger = LoggerFactory.getLogger(FeeCalculatorUtils.class);
	private static final int UNIT_INSUR_AMT_INIT = 35;
	
	private static final Integer SERVICE_CHARGE_FEE = 20;
	
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
	
	//1亿表示100万以上区间
	private static final int[] TOTAL_AMT_RANGE = {80000, 100000, 120000, 140000 ,160000, 180000, 200000, 250000,300000,400000,1000000,100000000};
	private static final int[] TOTAL_AMT_LEVEL = {0, 1000,  1200,  1500,   2000,   2500,  3000, 3500, 5000, 10000, 15000, 50000};
	private static final String CAREEPOSIT = "1"; //租车押金
	private static final String CAREEPOSITDEFULT = "3"; //默认租车押金
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
	 * 平台手续费
	 */
	public Integer calServiceChargeFee() {
		return SERVICE_CHARGE_FEE;
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
		}
		if (insuranceConfigs == null || insuranceConfigs.isEmpty()) {
			return unitInsurAmt;
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
	
	/**
	 * 计算车辆押金
	 * @param InternalStaff
	 * @param cityCode
	 * @param guidPrice
	 * @param carBrandTypeRadio
	 * @param carYearRadio
	 * @param depositList
	 * @param reliefPercetage
	 * @return
	 */
	public static CarDepositAmtVO calCarDepositAmt(Integer InternalStaff, Integer cityCode, Integer guidPrice, Double carBrandTypeRadio, Double carYearRadio, List<DepositText> depositList, Double reliefPercetage) {
		if (INTERNAL_STAFF_FLAG.equals(InternalStaff)) {
			return calCarDepositAmt();
		} else {
			return calCarDepositAmt(cityCode, guidPrice, carBrandTypeRadio, carYearRadio, depositList, reliefPercetage);
		}
	}
	
	/**
	 * 计算车辆押金(内部员工)
	 * @return
	 */
	public static CarDepositAmtVO calCarDepositAmt() {
		CarDepositAmtVO carDepositAmtVO = new CarDepositAmtVO();
		carDepositAmtVO.setCarDepositAmt(RENT_ADJUST_AMT_INNER);
		carDepositAmtVO.setCarDepositRadio(1.0);
		carDepositAmtVO.setReliefPercetage(0.0);
		carDepositAmtVO.setReliefAmt(0);
		return carDepositAmtVO;
	}
	
	
	/**
	 * 计算车辆押金(外部员工)
	 * @param cityCode
	 * @param guidPrice
	 * @param carBrandTypeRadio
	 * @param carYearRadio
	 * @param depositList
	 * @param reliefPercetage
	 * @return
	 */
	public static CarDepositAmtVO calCarDepositAmt(Integer cityCode, Integer guidPrice, Double carBrandTypeRadio, Double carYearRadio, List<DepositText> depositList, Double reliefPercetage) {
		//初始化车辆押金
		Integer suggestTotal = getSuggestTotalAmt(guidPrice);
		Boolean carbool = true;
		String cityCodeStr = cityCode == null ? "":String.valueOf(cityCode);
		if(null!=depositList&&depositList.size()>0){
			for (DepositText dt : depositList) {
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
				for (DepositText dt : depositList) {
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
			reliefPercetage = 0.0;
			carDepositAmt = suggestTotal;
		} else {
			carDepositAmt = suggestTotal * (1-reliefPercetage) * coefficient;
		}
		CarDepositAmtVO carDepositAmtVO = new CarDepositAmtVO();
		carDepositAmtVO.setCarDepositAmt((int) carDepositAmt);
		carDepositAmtVO.setCarDepositRadio(coefficient);
		carDepositAmtVO.setReliefPercetage(reliefPercetage);
		carDepositAmtVO.setReliefAmt((int) (reliefPercetage*suggestTotal*coefficient));
		return carDepositAmtVO;
	}
	
	/**
	 * 根据车辆的购买价格计算建议的租车押金（预授权额度）
	 * @param surplusPrice
	 * @return
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
	 * @param internalStaff
	 * @param cityCode
	 * @param carPlateNum
	 * @param specialCityCodes
	 * @param specialIllegalDepositAmt
	 * @param illegalDepositList
	 * @param rentTime
	 * @param revertTime
	 * @return
	 */
	public static Integer calIllegalDepositAmt(Integer internalStaff, Integer cityCode, String carPlateNum, String specialCityCodes, Integer specialIllegalDepositAmt,
			List<IllegalDepositConfig> illegalDepositList, LocalDateTime rentTime, LocalDateTime revertTime) {
		internalStaff = internalStaff == null ? 0:internalStaff;
		Integer illegalDepositAmt = ILLEGAL_DEPOSIT.get(0);
		if (carPlateNum != null && !"".equals(carPlateNum) && specialCityCodes != null && !"".equals(specialCityCodes)) {
			if("粤".equals(carPlateNum.substring(0,1)) && specialCityCodes.contains(String.valueOf(cityCode))){
				illegalDepositAmt = specialIllegalDepositAmt == null ? illegalDepositAmt:specialIllegalDepositAmt;
				return illegalDepositAmt;
	        }
		}
		if (internalStaff == 1) {
			return ILLEGAL_DEPOSIT.get(internalStaff);
		}
		illegalDepositAmt = getIllegalDepositAmt(cityCode, illegalDepositList, rentTime, revertTime);
		return illegalDepositAmt;
	}
	
	
	/**
	 * 通过配置获取对应的违章押金
	 * @param cityCode
	 * @param illegalDepositList
	 * @param rentTime
	 * @param revertTime
	 * @return
	 */
	public static Integer getIllegalDepositAmt(Integer cityCode, List<IllegalDepositConfig> illegalDepositList, LocalDateTime rentTime, LocalDateTime revertTime) {
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
		for (IllegalDepositConfig idc:illegalDepositList) {
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
	 * @param dayMileage
	 * @param guideDayPrice
	 * @param getmileage
	 * @param returnMileage
	 * @param rentTime
	 * @param revertTime
	 * @param configHours
	 * @return
	 */
	public static Integer calMileageAmt(Integer dayMileage, Integer guideDayPrice, Integer getmileage, Integer returnMileage, LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours) {
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
	 * @param cityCode
	 * @param oilVolume
	 * @param engineType
	 * @param getOilScale
	 * @param returnOilScale
	 * @param oilAverageList
	 * @param oilScaleDenominator
	 * @return
	 */
	public static Integer calOilAmt(Integer cityCode, Integer oilVolume, Integer engineType, Integer getOilScale, Integer returnOilScale, List<OilAverageCostBO> oilAverageList, Integer oilScaleDenominator) {
		//动力类型，1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5
		if (engineType==null||oilVolume==null||oilVolume==0||getOilScale==null||returnOilScale==null) {
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
