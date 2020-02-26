package com.autoyol.platformcost;

import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.common.entity.OilAverageCostEntity;
import com.autoyol.platformcost.enums.ExceptionCodeEnum;
import com.autoyol.platformcost.exception.OwnerFeeCostException;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.FeeResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param configHours 配置小时数
	 * @param carPriceOfDayList 车辆天对应价格列表
	 * @return FeeResult
	 */
	public static FeeResult calRentAmt(LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours, List<CarPriceOfDay> carPriceOfDayList) {
		if (rentTime == null) {
			throw new OwnerFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new OwnerFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
		}
		if (carPriceOfDayList == null || carPriceOfDayList.isEmpty()) {
			throw new OwnerFeeCostException(ExceptionCodeEnum.CAR_DAY_PRICE_LIST_IS_EMPTY);
		}
		// 计算日均价
		Integer holidayAverage = RenterFeeCalculatorUtils.getHolidayAverageRentAMT(rentTime, revertTime, carPriceOfDayList);
		// 计算总租期
		Double rentDays = CommonUtils.getRentDays(rentTime, revertTime, configHours);
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
	 * 计算超里程费用
	 * @param carOwnerType 车辆类型
	 * @param dayMileage 日均限里程数
	 * @param guideDayPrice 车辆日租金指导价
	 * @param getmileage 取车里程数
	 * @param returnMileage 还车里程数
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param configHours 配置小时数
	 * @return Integer
	 */
	public static Integer calMileageAmt(Integer carOwnerType, Integer dayMileage, Integer guideDayPrice, Integer getmileage, Integer returnMileage, LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours) {
		if (rentTime == null) {
			throw new OwnerFeeCostException(ExceptionCodeEnum.RENT_TIME_IS_NULL);
		}
		if (revertTime == null) {
			throw new OwnerFeeCostException(ExceptionCodeEnum.REVERT_TIME_IS_NULL);
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
		Integer mileageAmt = (int) Math.ceil(CommonUtils.mul(distance, radio));  // 超出距离 * 系数
		if (CommonUtils.isEscrowCar(carOwnerType)) {
			mileageAmt = 0;
		}
		return mileageAmt;
	}
	
	
	/**
	 * 计算油费
	 * @param carOwnerType 车辆类型
	 * @param cityCode 城市编号
	 * @param oilVolume 油箱容量
	 * @param engineType 油品类型
	 * @param getOilScale 取车油表刻度
	 * @param returnOilScale 还车油表刻度
	 * @param oilAverageList 油费配置列表
	 * @param oilScaleDenominator 总刻度数
	 * @return Integer
	 */
	public static Integer calOilAmt(Integer carOwnerType, Integer cityCode, Integer oilVolume, Integer engineType, Integer getOilScale, Integer returnOilScale, List<OilAverageCostEntity> oilAverageList, Integer oilScaleDenominator) {
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
		if (CommonUtils.isEscrowCar(carOwnerType)) {
			oilCost = 0;
		}
		return oilCost;
	}
	
	
	/**
	 * 计算gps服务费
	 * @param lsRules gps服务费计费规则信息
	 * @param lsGpsSerialNumber 车辆安装gps序列号列表
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return Integer
	 */
	public static List<FeeResult> calGpsServiceAmt(List<CarGpsRuleEntity> lsRules, List<Integer> lsGpsSerialNumber, LocalDateTime rentTime, LocalDateTime revertTime) {
		if (rentTime == null || revertTime == null) {
			return null;
		}
		// 获取租期
		Integer days = (int) CommonUtils.getDaysUpCeil(rentTime, revertTime);
		Integer gpsServiceAmt = 0;
		List<FeeResult> feeResultList = new ArrayList<FeeResult>();
		if (lsRules == null || lsRules.isEmpty()) {
			// 小于15天的订单为5元/天，大于（含）15天的订单为2元/天
        	if (days < 15) {
        		gpsServiceAmt = days * GPS_DAY_SERVICE_COST;
        		feeResultList.add(new FeeResult(GPS_DAY_SERVICE_COST, (double)days, gpsServiceAmt));
        	} else {
        		int more15 = days - 14;
        		gpsServiceAmt = more15 * GPS_DAY_SERVICE_COST_15 + ((days - more15) * GPS_DAY_SERVICE_COST);
        		feeResultList.add(new FeeResult(GPS_DAY_SERVICE_COST_15, (double)more15, more15 * GPS_DAY_SERVICE_COST_15));
        		feeResultList.add(new FeeResult(GPS_DAY_SERVICE_COST, (double)(days - more15), (days - more15) * GPS_DAY_SERVICE_COST));
        	}
		} else {
			feeResultList = calcGpsAmtAndSaveLogMap(lsGpsSerialNumber, lsRules, days);
		}
		return feeResultList;
	}
	
	
	/**
	 * 按配置规则获取gps服务费
	 * @param lsGps 车辆安装gps序列号列表
	 * @param lsRules gps服务费计费规则信息
	 * @param rentDays 租期
	 * @return int
	 */
	public static List<FeeResult> calcGpsAmtAndSaveLogMap(List<Integer> lsGps, List<CarGpsRuleEntity> lsRules, Integer rentDays) {
		if (lsGps == null || lsGps.isEmpty()) {
			return null;
		}
		if (lsRules == null || lsRules.isEmpty()) {
			return null;
		}
		if (rentDays == null) {
			return null;
		}
		List<FeeResult> feeResultList = new ArrayList<FeeResult>();
		// list转map
		Map<Integer,CarGpsRuleEntity> mapRules = lsRules.stream().collect(Collectors.toMap(CarGpsRuleEntity::getSerialNumber, carGpsRule->carGpsRule));
		for (Iterator<Integer> iterator = lsGps.iterator(); iterator.hasNext();) {
			Integer serialNumber = iterator.next();
            CarGpsRuleEntity carGpsRule = mapRules.get(serialNumber);
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
					Integer curGpsAmt = (rentDays - pre) * Integer.valueOf(fees[i]).intValue();
					//gpsAmt += (rentDays - pre) * Integer.valueOf(fees[i]).intValue();
					feeResultList.add(new FeeResult(Integer.valueOf(fees[i]), (double)(rentDays - pre), curGpsAmt));
					break;
				}else{
					Integer curGpsAmt = Integer.valueOf(days[i]).intValue() * Integer.valueOf(fees[i]).intValue();
					//gpsAmt += Integer.valueOf(days[i]).intValue() * Integer.valueOf(fees[i]).intValue();
					pre += Integer.valueOf(days[i]).intValue();
					feeResultList.add(new FeeResult(Integer.valueOf(fees[i]), Double.valueOf(days[i]), curGpsAmt));
				}
			}
			
		}
		return feeResultList;
	}
	
	/**
	 * 计算车主取车服务费
	 * @param carOwnerType 车辆类型
	 * @param srvGetFlag 取车标志
	 * @return Integer
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
	 * @param carOwnerType 车辆类型
	 * @param srvReturnFlag 还车标志
	 * @return Integer
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
	 * 收取车主端平台服务费
	 * @param rentAmt 租金
	 * @param serviceProportion 服务费比例
	 * @return Integer
	 */
    public static Integer calServiceExpense(Integer rentAmt, Integer serviceProportion) {
    	Integer serviceExpense = 0;
    	//取车辆的服务费收取比例，服务费的最大最小限额
    	if(serviceProportion != null && serviceProportion != 0 && rentAmt != null){
            //改成向上取整
            serviceExpense = new BigDecimal(rentAmt).multiply(new BigDecimal(serviceProportion)).divide(new BigDecimal(100),0,RoundingMode.UP).intValue();
    	}else{
    		serviceExpense = 0 ;//收费比例为0，不收取平台服务费
    	}
    	return serviceExpense;
    }
    
    
    /**
	 * 收取车主端代步车服务费
	 * @param rentAmt 租金
	 * @param proxyProportion 代管车比例
	 * @return Integer
	 */
    public static Integer calProxyExpense(Integer rentAmt, Integer proxyProportion) {
    	Integer proxyExpense = 0;
    	//收取代管车服务费
    	if(proxyProportion != null && proxyProportion != 0 && rentAmt != null){
            //改成向上取整
            BigDecimal divide = new BigDecimal(rentAmt).multiply(new BigDecimal(proxyProportion)).divide(new BigDecimal(100),0,RoundingMode.UP);
            proxyExpense = divide.intValue();
    	}else{
    		proxyExpense = 0; //收费比例为0，不收取代管车服务费
    	}
    	return proxyExpense;
    }
    
}
