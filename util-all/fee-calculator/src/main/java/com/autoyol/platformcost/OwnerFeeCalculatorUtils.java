package com.autoyol.platformcost;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.autoyol.platformcost.model.CarGpsRule;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.FeeResult;
import com.autoyol.platformcost.model.OilAverageCostBO;
import com.autoyol.platformcost.model.ServiceExpenseInfo;

public class OwnerFeeCalculatorUtils {
	
	// gps 单日收取费用
    private static final int GPS_DAY_SERVICE_COST = 5;
    // 订单超过15天（含）的订单
    private static final int GPS_DAY_SERVICE_COST_15 = 2;
    // 车主取车服务费
    private static final int OWNER_SRV_GET_AMT = 10;
    // 车主还车服务费
	private static final int OWNER_SRV_RETURN_AMT = 10;
	
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
		Integer holidayAverage = RenterFeeCalculatorUtils.getHolidayAverageRentAMT(rentTime, revertTime, carPriceOfDayList);
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
	public static Integer calMileageAmt(Integer carOwnerType, Integer dayMileage, Integer guideDayPrice, Integer getmileage, Integer returnMileage, LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours) {
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
		Integer mileageAmt = (int) Math.ceil(CommonUtils.mul(distance, radio));  // 超出距离 * 系数
		if (CommonUtils.isEscrowCar(carOwnerType)) {
			mileageAmt = 0;
		}
		return mileageAmt;
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
	public static Integer calOilAmt(Integer carOwnerType, Integer cityCode, Integer oilVolume, Integer engineType, Integer getOilScale, Integer returnOilScale, List<OilAverageCostBO> oilAverageList, Integer oilScaleDenominator) {
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
		if (CommonUtils.isEscrowCar(carOwnerType)) {
			oilCost = 0;
		}
		return oilCost;
	}
	
	
	/**
	 * 计算gps服务费
	 * @param lsRules
	 * @param lsGpsSerialNumber
	 * @param rentTime
	 * @param revertTime
	 * @return
	 */
	public static Integer calGpsServiceAmt(List<CarGpsRule> lsRules, List<Integer> lsGpsSerialNumber, LocalDateTime rentTime, LocalDateTime revertTime) {
		if (rentTime == null || revertTime == null) {
			return 0;
		}
		// 获取租期
		Integer days = (int) CommonUtils.getDaysUpCeil(rentTime, revertTime);
		Integer gpsServiceAmt = 0;
		if (lsRules == null || lsRules.isEmpty()) {
			// 小于15天的订单为5元/天，大于（含）15天的订单为2元/天
        	if (days < 15) {
        		gpsServiceAmt = days * GPS_DAY_SERVICE_COST;
        	} else {
        		int more15 = days - 14;
        		gpsServiceAmt = more15 * GPS_DAY_SERVICE_COST_15 + ((days - more15) * GPS_DAY_SERVICE_COST);
        	}
		} else {
			gpsServiceAmt = calcGpsAmtAndSaveLogMap(lsGpsSerialNumber, lsRules, days);
		}
		return gpsServiceAmt;
	}
	
	
	/**
	 * 按配置规则获取gps服务费
	 * @param lsGps
	 * @param lsRules
	 * @param rentDays
	 * @return
	 */
	public static int calcGpsAmtAndSaveLogMap(List<Integer> lsGps, List<CarGpsRule> lsRules, Integer rentDays) {
		if (lsGps == null || lsGps.isEmpty()) {
			return 0;
		}
		if (lsRules == null || lsRules.isEmpty()) {
			return 0;
		}
		if (rentDays == null) {
			return 0;
		}
		int gpsAmt = 0;
		// list转map
		Map<Integer,CarGpsRule> mapRules = lsRules.stream().collect(Collectors.toMap(CarGpsRule::getSerialNumber, carGpsRule->carGpsRule));
		for (Iterator<Integer> iterator = lsGps.iterator(); iterator.hasNext();) {
			Integer serialNumber = iterator.next();
			CarGpsRule carGpsRule = mapRules.get(serialNumber);
			if(carGpsRule == null){
				continue;
			}
			String[] days = (carGpsRule.getDays() != null && !"".equals(carGpsRule.getDays()))?carGpsRule.getDays().split(","):null;
			String[] fees = (carGpsRule.getFees() != null && !"".equals(carGpsRule.getFees()))?carGpsRule.getFees().split(","):null;
			if(days == null || fees == null){
				continue;
			}
			if(days != null && fees != null && days.length != fees.length){
				continue;
			}
			int pre = 0;  //上一次的累计天数
			for (int i = 0; i < days.length; i++) {
				if(rentDays <= Integer.valueOf(days[i]).intValue()){
					gpsAmt += (rentDays - pre) * Integer.valueOf(fees[i]).intValue();
					break;
				}else{
					gpsAmt += Integer.valueOf(days[i]).intValue() * Integer.valueOf(fees[i]).intValue();
					pre += Integer.valueOf(days[i]).intValue();
				}
			}
			
		}
		return gpsAmt;
	}
	
	/**
	 * 计算车主取车服务费
	 * @param carOwnerType
	 * @param srvGetFlag
	 * @return
	 */
	public static Integer calOwnerSrvGetAmt(Integer carOwnerType, Integer srvGetFlag) {
		if (srvGetFlag == null || srvGetFlag == 0) {
			return 0;
		}
		if (CommonUtils.isEscrowCar(carOwnerType)) {
			return 0;
		}
		return OWNER_SRV_GET_AMT;
	}
	
	
	/**
	 * 获取车主还车服务费
	 * @param carOwnerType
	 * @param srvReturnFlag
	 * @return
	 */
	public static Integer calOwnerSrvReturnAmt(Integer carOwnerType, Integer srvReturnFlag) {
		if (srvReturnFlag == null || srvReturnFlag == 0) {
			return 0;
		}
		if (CommonUtils.isEscrowCar(carOwnerType)) {
			return 0;
		}
		return OWNER_SRV_RETURN_AMT;
	}
	
	
	/**
     * 兼容长租平台服务费
     * @param service_proportion
     * @param service_max_value
     * @param service_min_value
     * @param rentAmt
     * @param modifyPrice
     * @param proxy_proportion
     * @return
     */
    public ServiceExpenseInfo calServiceExpenseLongRent(Integer isLongRent,Integer longServiceAmt,Integer serviceProportion, Integer rentAmt, Integer proxyProportion){
		Integer serviceExpense = 0 ;
	    ServiceExpenseInfo serviceExpenseInfo = new ServiceExpenseInfo();
	    isLongRent = isLongRent == null ? 0:isLongRent;
		Integer proxyExpense = 0 ;
	    if(isLongRent == 0){
			//取车辆的服务费收取比例，服务费的最大最小限额
	    	if(serviceProportion != null && serviceProportion != 0 && rentAmt != null){
                //改成向上取整
                serviceExpense = new BigDecimal(rentAmt).multiply(new BigDecimal(serviceProportion)).divide(new BigDecimal(100),0,RoundingMode.UP).intValue();
	    	}else{
	    		serviceExpense = 0 ;//收费比例为0，不收取平台服务费
	    	}
	    }else{
	    	if(longServiceAmt == null || longServiceAmt < 0){
	    		longServiceAmt = 0;  //如果平台服务费小于0，默认为0
	    	}
	    	serviceExpense = longServiceAmt;
	    	
	    }
		//收取代管车服务费
    	if(proxyProportion != null && proxyProportion != 0 && rentAmt != null){
            //改成向上取整
            BigDecimal divide = new BigDecimal(rentAmt).multiply(new BigDecimal(proxyProportion)).divide(new BigDecimal(100),0,RoundingMode.UP);
            proxyExpense = divide.intValue();
    	}else{
    		proxyExpense = 0; //收费比例为0，不收取代管车服务费
    	}
	    serviceExpenseInfo.setServiceExpense(serviceExpense);
	    serviceExpenseInfo.setProxyExpense(proxyExpense);  //赋予值。
		return serviceExpenseInfo;
	}
    
}
