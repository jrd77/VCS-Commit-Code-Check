package com.autoyol.platformcost;

import com.atzuche.config.common.entity.OilAverageCostEntity;
import com.autoyol.platformcost.model.AbatementConfig;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.SphericalDistanceCoefficient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CommonUtils {
	/**
	 * 配置的一天小时
	 */
	public static final Integer CONFIGHOURS = 8;
	public final static double D_Mileage=0.005;//超日限里程系数
	public final static double MIN_Mileage=0.5;//超日限里最小每天
	public final static double MAX_Mileage=5;//超日限里程最大每天
	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;
	public final static String [] CAR_TYPE={"20","25","30","35"};//代管车  托管车
	/**
               * 最小距离处理
     */
    public static final Integer MIN_DISTANCE=0;
    /**
                * 米的转换
     */
    public static final Integer MULTIPLY=1000;
    // 修改地址距离原地址>1公里需要收费
    private static final Integer MODIFY_ADDR_NEED_CHARGE_DISTANCE = 1;
    
    private static final double COEFFICIENT_INIT = 1.0;
    
	private static final double COEFFICIENT_NOVICE = 1.2;
	
	private static final double EASYCOEFFICIENT_INIT = 1.0;
    
	private static final double EASYCOEFFICIENT_NOVICE = 1.3;
	
	private static final double DRIVERCOEFFICIENT_INIT = 1.0;
    
	private static final double DRIVERCOEFFICIENT_NOVICE = 1.3;
	
	private static final double DRIVER_SCORE_MIN = 30.0; 
	
	private static final String[] CAR_EASY_TAG = {"370","371"};
	
	public static final float FINE_AMT_RATIO_BIG = 0.3F;
	public static final float FINE_AMT_RATIO_SMALL = 0.2F;
	public static final double CANCEL_FINE_RATIO = 0.3;
	public static final Integer CANCEL_FINE_LIMIT = 500;
	public static final Integer BEFORE_TRANS_TIME_SPAN = 4;
	public static final Integer MODIFY_GET_FINE_AMT = 50;
	public static final Integer MODIFY_RETURN_FINE_AMT = 50;
	
	public static final String FORMAT_STR_RENYUN = "yyyy-MM-dd HH:mm";
	
	public static final String FORMAT_STR_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_STR_LONG = "yyyyMMddHHmmss";
	
	public static final String FORMAT_STR_DATE = "yyyy-MM-dd";
	
	public static final double INSURE_DISCOUNT_NORMAL = 1.0;
	public static final double INSURE_DISCOUNT_DIS = 0.7;
	public static final double INSURE_DISCOUNT_EIGHT = 0.8;
	public static final double INSURE_DISCOUNT_NINE = 0.9;
	/** 一天小时数  */
	private static final int HOURS_PER_DAY = 24;
	
	private static final int CARPURCHASEPRICE_150000 = 150000;
	private static final int CARPURCHASEPRICE_200000 = 200000;
	private static final int CARPURCHASEPRICE_300000 = 300000;
	
    /**
               * 初始化补充保障服务费单价配置
     */
    public static final List<AbatementConfig> ABATEMENTCONFIG_List = new ArrayList<AbatementConfig>() {
    	/**
		 * 
		 */
		private static final long serialVersionUID = -7734039511976536462L;

	{
        add(new AbatementConfig(0, 250000, 40, 40, 40, 40));
        add(new AbatementConfig(250000, 400000, 50, 50, 50, 50));
        add(new AbatementConfig(400000, 600000, 90, 90, 90, 90));
        add(new AbatementConfig(600000, 800000, 100, 100, 100, 100));
        add(new AbatementConfig(800000, 1000000, 120, 120, 120, 120));
        add(new AbatementConfig(1000000, 1500000, 225, 225, 225, 225));
        add(new AbatementConfig(1500000, 2000000, 300, 300, 300, 300));
        add(new AbatementConfig(2000000, 2500000, 375, 375, 375, 375));
        add(new AbatementConfig(2500000, 3000000, 450, 450, 450, 450));
        add(new AbatementConfig(3000000, 3500000, 525, 525, 525, 525));
        add(new AbatementConfig(3500000, 4000000, 600, 600, 600, 600));
        add(new AbatementConfig(4000000, 4500000, 675, 675, 675, 675));
        add(new AbatementConfig(4500000, Integer.MAX_VALUE, 750, 750, 750, 750));
    }};

	/**
	 * 获取向上取整天数
	 * 总小时/24 有余数加1天
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return long
	 */
	public static long getDaysUpCeil(LocalDateTime rentTime, LocalDateTime revertTime) {
		Duration duration = Duration.between(rentTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();
		if ((minutes - hours*60) > 0) {
			hours = hours + 1;
		}
		if ((hours - days*24) > 0) {
			days = days + 1;
		}
		return days;
	}
	
	
	/**
	   * 计算租期
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param configHours 配置小时
	 * @return Double
	 */
	public static Double getRentDays(LocalDateTime rentTime, LocalDateTime revertTime, Integer configHours) {
		if (configHours == null) {
			configHours = CONFIGHOURS;
		}
		Duration duration = Duration.between(rentTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();
		if ((minutes - hours*60) > 0) {
			hours = hours + 1;
		}
		BigDecimal bdPremium = BigDecimal.ZERO;
		if ((hours - days*24) <= configHours) {
			bdPremium = new BigDecimal(1).divide(new BigDecimal(8)).multiply(new BigDecimal(hours - days*24));
		} else {
			days = days+1;
		}
		Double rentDays = new BigDecimal(days).add(bdPremium).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		return rentDays;
	}
	
	
	/**
	 * 计算提前时间
	 * @param rentTime 取车时间
	 * @param getCarBeforeTime 提前时间（单位分钟）
	 * @return LocalDateTime
	 */
	public static LocalDateTime calBeforeTime(LocalDateTime rentTime, Integer getCarBeforeTime){
		if (rentTime == null) {
			return null;
		}
		if (getCarBeforeTime == null) {
			return rentTime;
		}
		LocalDateTime beforeTime = rentTime.minusMinutes(getCarBeforeTime);
		return beforeTime;
	}
	
	/**
	 * 计算延后时间
	 * @param revertTime 还车时间
	 * @param returnCarAfterTime 延后时间（单位分钟）
	 * @return LocalDateTime
	 */
	public static LocalDateTime calAfterTime(LocalDateTime revertTime, Integer returnCarAfterTime){
		if (revertTime == null) {
			return null;
		}
		if (returnCarAfterTime == null) {
			return revertTime;
		}
		LocalDateTime afterTime = revertTime.plusMinutes(returnCarAfterTime);
		return afterTime;
	}
	
	
	/**
	 * 计算总小时数（计算日均价用）
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return float
	 */
	public static float getTotalHoursByRentAveragePrice(LocalDateTime rentTime, LocalDateTime revertTime) {
		Duration duration = Duration.between(rentTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long leftMinutes = minutes - hours*60;
		float H = 0.00f;
		if(leftMinutes > 0 && leftMinutes <= 15){
			H = hours + 0.25f;  
		}else if(leftMinutes > 15 && leftMinutes <= 30){
			H = hours + 0.5f;  
		}else if(leftMinutes > 30 && leftMinutes <= 45){
			H = hours + 0.75f;  
		}else if(leftMinutes > 45 && leftMinutes <= 60){
			H = hours + 1f;  
		}else{
			H = hours;  //没有分钟的情况
		}
		return H;
	}

    public static void main(String[] args) {
        LocalDateTime rentTime = LocalDateTime.of(2020, 7, 25, 9, 30, 0);
        LocalDateTime revertTime = LocalDateTime.of(2020, 7, 25, 23, 59, 59);
        float totalHoursByRentAveragePrice = getTotalHoursByRentAveragePrice(rentTime, revertTime);
        System.out.println(totalHoursByRentAveragePrice);
    }
	
	/**
	 * 计算起租时间到这一天的24点的小时数
	 * @param rentTime 取车时间
	 * @param endDay 截止日期
	 * @return float
	 */
	public static float getHolidayTopHours(LocalDateTime rentTime,String endDay) {
		// 59:59:59  ---> 23:59:59  24小时制
		LocalDateTime endTime = LocalDateTime.parse(endDay+" 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //今天的截止时间
		Duration duration = Duration.between(rentTime, endTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long leftMinutes = minutes - hours*60;
		float H = 0.00f;
		if(leftMinutes > 0 && leftMinutes <= 15){
			H = hours + 0.25f;  
		}else if(leftMinutes > 15 && leftMinutes <= 30){
			H = hours + 0.5f;  
		}else if(leftMinutes > 30 && leftMinutes <= 45){
			H = hours + 0.75f;  
		}else if(leftMinutes > 45 && leftMinutes <= 60){
			H = hours + 1f;  
		}else{
			H = hours;  //没有分钟的情况
		}
		return H;
	}
	
	
	/**
	 * 计算起租时间到这一天的24点的小时数
	 * @param revertTime 还车时间
	 * @param beginDay 开始日期
	 * @return float
	 */
	public static float getHolidayFootHours(LocalDateTime revertTime,String beginDay) {
		// 59:59:59  ---> 23:59:59  24小时制
		LocalDateTime beginTime = LocalDateTime.parse(beginDay+" 00:00:01", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //今天的截止时间
		Duration duration = Duration.between(beginTime, revertTime);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long leftMinutes = minutes - hours*60;
		float H = 0.00f;
		if(leftMinutes > 0 && leftMinutes <= 15){
			H = hours + 0.25f;  
		}else if(leftMinutes > 15 && leftMinutes <= 30){
			H = hours + 0.5f;  
		}else if(leftMinutes > 30 && leftMinutes <= 45){
			H = hours + 0.75f;  
		}else if(leftMinutes > 45 && leftMinutes <= 60){
			H = hours + 1f;  
		}else{
			H = hours;  //没有分钟的情况
		}
		return H;
	}
	
	
	/**
	 * 根据起租时间和还车时间得到日期列表  MAIN
	 * //列表只有1个，代表的是当天。
	   //列表只有2个，代表的是两天都是不完整的。
	   //列表大于3个，中间才有完整的一天
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return List<String>
	 */
	public static List<LocalDate> getHolidayRentDays(LocalDateTime rentTime,LocalDateTime revertTime) {
		List<LocalDate> dateList = new ArrayList<LocalDate>();
		addHolidayRentDays(rentTime, revertTime, dateList);
		return dateList;
	}
	
	/**
	 * 获取天数
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @param dateList 日期列表
	 */
	private static void addHolidayRentDays(LocalDateTime rentTime, LocalDateTime revertTime, List<LocalDate> dateList) {
		dateList.add(rentTime.toLocalDate());

		int rentMonth = rentTime.getMonthValue();
		int revertMonth = revertTime.getMonthValue();
		
		int lastDay = revertTime.getDayOfMonth();
		int currentDay = rentTime.getDayOfMonth();

		//同月同日时表示结束
		if(currentDay==lastDay&&rentMonth==revertMonth) {
			return;
		}
		else {
			rentTime=rentTime.plusDays(1);
			addHolidayRentDays(rentTime, revertTime, dateList);
		}
	}
	
	/**
	 * 根据车辆购置年份获取当年末残值比例
	 * 车龄超过8年的车，残值按8年算；
	 * 不满一年的按一年算，比如3年5个月按4年算；
	 * @param carYear 车辆年份
	 * @return Integer
	 */
	public static Integer getSurplusPriceProYear(Double carYear){
		if (carYear == null) {
			return 3;
		}
		Integer newCarYear = 1;
		if(carYear < 1){
			newCarYear = 1;
		}else if(carYear > 1 && carYear < 8)	{
			newCarYear = (int) Math.ceil(carYear);
		}else if(carYear >= 8){
			newCarYear=8;
		}
		return newCarYear;
	}

	/**
	 * 获取新车押金系数
	 * @param year 车辆年份
	 * @param twoYearRatio 小于等于两年的系数
	 * @param twoYearLaterRatio 大于两年的系数
	 * @return Double
	 */
	public Double getNewCarCoefficient(Integer year, Double twoYearRatio, Double twoYearLaterRatio) {
		if (year == null) {
			return 1.0;
		}
		if (year <= 2) {
			return twoYearRatio == null ? 1.3:twoYearRatio;
		}else{
			return twoYearLaterRatio == null ? 1.0:twoYearLaterRatio;
		}
	}
	
	/**
	 * 提供精确的减法运算。
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差
	 */

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 两个参数的积
	 */

	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}
	
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}
	
	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * @param v1 被除数
	 * @param v2 除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			scale = 10;
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 获取油费单价
	 * @param engineType 油品
	 * @param cityCode 城市编号
	 * @param oilAverageList 油费单价配置列表
	 * @return Double
	 */
	public static Double getAverageCost(Integer engineType, Integer cityCode, List<OilAverageCostEntity> oilAverageList) {
		if (engineType == null) {
			return 0.0;
		}
		if (oilAverageList == null || oilAverageList.isEmpty()) {
			return 0.0;
		}
		cityCode = cityCode == null ? 0:cityCode;
		Double oilAverageCost = 0.0;
		for (OilAverageCostEntity oilAverage:oilAverageList) {
			if (cityCode.equals(oilAverage.getCityCode()) && 
					engineType.equals(oilAverage.getEngineType())) {
				Integer molecule = oilAverage.getMolecule();//分子
				Integer denominator = oilAverage.getDenominator();//分母
				if (molecule == null || denominator == null || denominator == 0) {
					oilAverageCost = 0.0;
				} else {
					oilAverageCost = (molecule * 1.0d) / denominator;
				}
				break;
			}
		}
		return oilAverageCost;
	}
	
	/**
	 * 是否托管车管车
	 * @param ownerType 车辆类型
	 * @return boolean
	 */
	public static boolean isEscrowCar(Integer ownerType) {
		if (ownerType == null) {
			return false;
		}
		if (ownerType != null && Arrays.asList(CAR_TYPE).contains(ownerType.toString())) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 计算本次修改是否需要收费
	 * @param localDateTime 待确认时间
	 * @param now 当前时间
	 * @param beforeHour 提前小时数
	 * @param serviceLon 修改经度
	 * @param serviceLat 修改纬度
	 * @param currentServiceLon 当前经度
	 * @param currentServiceLat 当前纬度
	 * @param sphericalDistanceCoefficients 系数配置列表
	 * @return boolean
	 */
    public static boolean isNeedCharge(LocalDateTime localDateTime, LocalDateTime now, Integer beforeHour,
                                 String serviceLon, String serviceLat, String currentServiceLon, String currentServiceLat,List<SphericalDistanceCoefficient> sphericalDistanceCoefficients) {
        return isTimeNeedCharge(localDateTime, now, beforeHour)
                && isAddressNeedCharge(serviceLon, serviceLat, currentServiceLon, currentServiceLat, sphericalDistanceCoefficients);
    }
	
	
	/**
     * 时间是否命中收费规则,4小时前免费，4小时内收费。
     * @param localDateTime 待确认时间
     * @param now 当前时间
     * @param beforeHour 提前小时数
     * @return boolean
     */
    public static boolean isTimeNeedCharge(LocalDateTime localDateTime, LocalDateTime now, Integer beforeHour) {
        return now.isAfter(localDateTime.minusHours(beforeHour)) || now.isAfter(localDateTime) ;
    }
	
	
	/**
	 * 地址是否命中收费规则
	 * @param serviceLon 修改经度
	 * @param serviceLat 修改纬度
	 * @param currentServiceLon 当前经度
	 * @param currentServiceLat 当前纬度
	 * @param sphericalDistanceCoefficients 系数配置列表
	 * @return boolean
	 */
    public static boolean isAddressNeedCharge(String serviceLon, String serviceLat, String currentServiceLon, String currentServiceLat, List<SphericalDistanceCoefficient> sphericalDistanceCoefficients) {
        if (serviceLon == null || "".equals(serviceLon)) {
        	return false;
        }
        if (serviceLat == null || "".equals(serviceLat)) {
        	return false;
        }
        if (currentServiceLon == null || "".equals(currentServiceLon)) {
        	return false;
        }
        if (currentServiceLat == null || "".equals(currentServiceLat)) {
        	return false;
        }

        Long realDistance = getRealMDistance(Double.valueOf(serviceLon),
                Double.valueOf(serviceLat), Double.valueOf(currentServiceLon),
                Double.valueOf(currentServiceLat), sphericalDistanceCoefficients);
        Integer distance = (int) (realDistance / 1000);
        return distance > MODIFY_ADDR_NEED_CHARGE_DISTANCE;
    }
	
	
	/**
	 * 计算获取实际距离（以米为单位
	 * @param carLon 车辆经度
	 * @param carLat 车辆纬度
	 * @param origionCarLon 原来经度
	 * @param originCarLat 原来纬度
	 * @param sphericalDistanceCoefficients 系数配置列表
	 * @return Long
	 */
    public static Long getRealMDistance(double carLon,double carLat,double origionCarLon,double originCarLat, List<SphericalDistanceCoefficient> sphericalDistanceCoefficients){
        float realDistance = getRealDistance(carLon, carLat, origionCarLon, originCarLat, sphericalDistanceCoefficients);
        BigDecimal bigDecimal = BigDecimal.valueOf(realDistance);
        return bigDecimal.multiply(BigDecimal.valueOf(MULTIPLY)).longValue();
    }
	
	
	/**
	 * 计算获取实际距离
	 * @param carLon 车辆经度
	 * @param carLat 车辆纬度
	 * @param origionCarLon 原来经度
	 * @param originCarLat 原来纬度
	 * @param sphericalDistanceCoefficients 系数配置列表
	 * @return float
	 */
    public static float getRealDistance(double carLon, double carLat, double origionCarLon, double originCarLat, List<SphericalDistanceCoefficient> sphericalDistanceCoefficients) {
        double distance = calcDistance(carLon, carLat, origionCarLon, originCarLat);
        //获取系数
        double coefficient = calcCoefficient(distance, sphericalDistanceCoefficients);
        //四舍五入（保留一位小数） 
        return BigDecimal.valueOf(distance).multiply(BigDecimal.valueOf(coefficient)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    
    /**
     * 计算系数
     * @param distance 球面距离（单位:公里）
     * @param coefficients 系数配置
     * @return double
     */
    public static double calcCoefficient(double distance, List<SphericalDistanceCoefficient> coefficients){
        //设置默认值
        SphericalDistanceCoefficient defaultCoefficient=new SphericalDistanceCoefficient();
        defaultCoefficient.setCoefficient(100);
        //获取系数
        SphericalDistanceCoefficient sphericalDistanceCoefficient = coefficients.stream().filter(coefficien -> rangeDistance(distance, coefficien)).findFirst().orElse(defaultCoefficient);
        return BigDecimal.valueOf(sphericalDistanceCoefficient.getCoefficient()).divide(BigDecimal.valueOf(100)).doubleValue();
    }
    
    /**
     * 比较距离
     * @param distance 距离
     * @param conf 配置
     * @return boolean
     */
    public static boolean rangeDistance(double distance,SphericalDistanceCoefficient conf){
        Optional<Integer> minDistance=Optional.ofNullable(conf.getMinDistance());
        Optional<Integer> maxDistance=Optional.ofNullable(conf.getMaxDistance());
        int min = minDistance.orElse(MIN_DISTANCE).intValue();
        int max = maxDistance.orElse(Integer.MAX_VALUE).intValue();
        if (distance>min && distance<=max) return true;
        return false;
    }
	
	/**
	 * 计算距离 (和数据库算法统一)
	 * @param carLon 车辆经度
	 * @param carLat 车辆纬度
	 * @param origionCarLon 原来经度
	 * @param originCarLat 原来纬度
	 * @return double
	 */
	public static double calcDistance(double carLon,double carLat,double origionCarLon,double originCarLat){
		return new BigDecimal(
				6378.137*2*Math.asin(Math.sqrt(Math.pow(Math.sin( (originCarLat*Math.PI/180-carLat*Math.PI/180)/2),2)
						+Math.cos(originCarLat*Math.PI/180)*Math.cos(carLat*Math.PI/180)*
						Math.pow(Math.sin( (origionCarLon*Math.PI/180-carLon*Math.PI/180)/2),2))))
				.doubleValue();
	}
	
	/**
	 * 日期价格列表去重
	 * @param carPriceOfDayList
	 * @return List<CarPriceOfDay>
	 */
	public static List<CarPriceOfDay> distinctCarPriceOfDayList(List<CarPriceOfDay> carPriceOfDayList) {
		if (carPriceOfDayList == null || carPriceOfDayList.isEmpty()) {
			return null;
		}
		return carPriceOfDayList.stream().collect(Collectors.collectingAndThen(
	                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CarPriceOfDay::getCurDate))), ArrayList::new));
	}
	
	
	/**
	 * 计算保险系数
	 * @param driLicFirstTime 初次领证日期
	 * @return Double
	 */
	public static Double getDriveAgeCoefficientByDri(LocalDate driLicFirstTime) {
		Double coefficient = COEFFICIENT_INIT;
		if (driLicFirstTime != null) {
			Long begin = driLicFirstTime.toEpochDay();
			Long end = LocalDate.now().toEpochDay();
			if (begin != null && end != null && (end - begin) < 365) {
				coefficient = COEFFICIENT_NOVICE;
			}
		}
		return coefficient;
	}
	
	/**
	 * 获取跑车/性能车系数
	 * @param labelIds 车辆标签
	 * @return Double
	 */
	public static Double getEasyCoefficient(List<String> labelIds) {
		Double easyCoefficient = EASYCOEFFICIENT_INIT;
		if (labelIds == null || labelIds.isEmpty()) {
			return EASYCOEFFICIENT_INIT;
		}
		List<String> carEasyTags = Arrays.asList(CAR_EASY_TAG);
		for (String labelId:labelIds) {
			if (labelId != null && carEasyTags.contains(labelId)) {
				easyCoefficient = EASYCOEFFICIENT_NOVICE;
				break;
			}
		}
		return easyCoefficient;
	}
	
	/**
	 * 格式化LocalDateTime
	 * @param localDateTime
	 * @param formatStr
	 * @return String
	 */
	public static String formatTime(LocalDateTime localDateTime, String formatStr) {
		if (localDateTime == null || StringUtils.isBlank(formatStr)) {
			return "";
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatStr);
		return dateTimeFormatter.format(localDateTime);
	}
	
	/**
	 * string转LocalDateTime
	 * @param dateTime
	 * @param parseStr
	 * @return LocalDateTime
	 */
	public static LocalDateTime parseTime(String dateTime, String parseStr) {
		if (StringUtils.isBlank(dateTime) || StringUtils.isBlank(parseStr)) {
			return null;
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(parseStr);     
        return LocalDateTime.parse(dateTime, dateTimeFormatter);
	}
	
	/**
	 * 获取平台保证费和补充保障服务费折扣
	 * @param rentTime 取车时间
	 * @param revertTime 还车时间
	 * @return double
	 */
	public static double getInsureDiscount(LocalDateTime rentTime,LocalDateTime revertTime,Integer inmsrpGuidePrice) {
		if (rentTime == null || revertTime == null) {
			return INSURE_DISCOUNT_NORMAL;
		}
		try {
			Duration duration = Duration.between(rentTime,revertTime);
			// 获取相差的分钟数
			long minutes = duration.toMinutes();
			// 如果24h制租期大于7天，平台保障费和补充保障服务费打7折
			if (minutes >= HOURS_PER_DAY*7*60) {
				// 计算保费指导价
				int guidePrice = inmsrpGuidePrice == null ? 0:inmsrpGuidePrice;
				if (guidePrice <= CARPURCHASEPRICE_150000) {
					return INSURE_DISCOUNT_DIS;
				} else if (guidePrice <= CARPURCHASEPRICE_200000) {
					return INSURE_DISCOUNT_EIGHT;
				} else if (guidePrice <= CARPURCHASEPRICE_300000) {
					return INSURE_DISCOUNT_NINE;
				} else {
					return INSURE_DISCOUNT_NORMAL;
				}
			}
		} catch (Exception e) {
			log.error("CommonUtils.getInsureDiscount has exception rentTime=[{}],revertTime=[{}]",rentTime,revertTime,e);
		}
		return INSURE_DISCOUNT_NORMAL;
	}
	
	
	public static Double getDriverCoefficient(String driverScore) {
		if (StringUtils.isBlank(driverScore)) {
			return DRIVERCOEFFICIENT_INIT;
		}
		try {
			double driverScoreD = Double.valueOf(driverScore);
			if (driverScoreD <= DRIVER_SCORE_MIN) {
				return DRIVERCOEFFICIENT_NOVICE;
			}
		} catch (Exception e) {
			log.error("getDriverCoefficient driverScore数据异常");
		}
		return DRIVERCOEFFICIENT_INIT;
	}
	

}
