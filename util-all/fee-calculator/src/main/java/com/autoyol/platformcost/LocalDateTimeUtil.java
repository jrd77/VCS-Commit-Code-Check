package com.autoyol.platformcost;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/18 11:48 上午
 **/
public class LocalDateTimeUtil {

    public static final int ONE_DAY=1;

    public static final LocalTime ZERO_LOCAL_TIME=LocalTime.of(0,0);

    public static final int MINUTES_OF_HOUR=60;


    static class HourOfDay{
        LocalDate date;
        float hours;

        public HourOfDay(LocalDate date, float hours) {
            this.date = date;
            this.hours = hours;
        }
    }

    /**
     * 计算当天到24点之间的minutes
     * @param now
     * @return
     */
    public static long computeDayLeftMinutes(LocalDateTime now){
         if(now==null){
             throw new IllegalArgumentException("localDateTime cannot be null,please check");
         }
         LocalDateTime end = LocalDateTime.of(now.toLocalDate().plusDays(ONE_DAY),ZERO_LOCAL_TIME);
         return Duration.between(now,end).toMinutes();
    }
    /**
     * 计算当天到24点之间的小时数
     * @param now
     * @return
     */
    public static float  computeDayLeftHours(LocalDateTime now){
        return computeDayLeftMinutes(now)/1.0f/MINUTES_OF_HOUR;
    }

    /**
     * 计算两个时间的分钟数
     * @param beginTime
     * @param endTime
     * @return
     */
    public static long computeSpaceMinutes(LocalDateTime beginTime,LocalDateTime endTime){
        if(beginTime ==null|| endTime ==null){
            throw new IllegalArgumentException("beginDate or endDate is null,please check:beginDate="+ beginTime +",endDate="+ endTime);
        }
        if(beginTime.isAfter(endTime)){
            throw new IllegalArgumentException("beginDate must before endDate");
        }

        return Duration.between(beginTime,endTime).toMinutes();
    }

    /**
     * 计算两个时间之间的小时数
     * @param beginTime
     * @param endTime
     * @return
     */
    public static float computeSpaceHours(LocalDateTime beginTime,LocalDateTime endTime){
        return computeSpaceMinutes(beginTime,endTime)/1.0f/MINUTES_OF_HOUR;
    }

    /**
     * 计算当天从0点到给定时间之间的分钟数
     * @param now 给定的时间
     * @return
     */
    public static long computeDayElapsedMinutes(LocalDateTime now){
        if(now==null){
            throw new IllegalArgumentException("localDateTime cannot be null,please check");
        }
        LocalDateTime beginTime = LocalDateTime.of(now.toLocalDate().minusDays(ONE_DAY),ZERO_LOCAL_TIME);
        return Duration.between(beginTime,now).toMinutes();
    }
    /**
     * 计算当天从0点到给定时间之间的小时数
     * @param now 给定的时间
     * @return
     */
    public static float computeDayElapsedHours(LocalDateTime now){
        return computeDayElapsedMinutes(now)/1.0f/MINUTES_OF_HOUR;
    }

    /**
     * 计算两个时间每天的小时数，返回响应结果
     * @param beginTime
     * @param endTime
     * @return
     */
    public static List<HourOfDay> computeHoursOfDay(LocalDateTime beginTime, LocalDateTime endTime){
         if(beginTime ==null|| endTime ==null){
             throw new IllegalArgumentException("beginDate or endDate is null,please check:beginDate="+ beginTime +",endDate="+ endTime);
         }
         if(beginTime.isAfter(endTime)){
             throw new IllegalArgumentException("beginDate must before endDate");
         }

         List<HourOfDay> hourOfDayList = new ArrayList<>();

        LocalDate beginDate = beginTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        if(beginDate.isEqual(endDate)){
            LocalDate today = beginTime.toLocalDate();
            float hours = computeSpaceHours(beginTime,endTime);
            hourOfDayList.add(new HourOfDay(today,hours));
        }else{
            List<LocalDate> betweenDays = computeBetweenDays(beginTime,endTime);
            Iterator<LocalDate> iterator = betweenDays.iterator();
            while (iterator.hasNext()){
                LocalDate it = iterator.next();
                if(it.isEqual(beginDate)){
                    float hours = computeDayLeftHours(beginTime);
                    hourOfDayList.add(new HourOfDay(it,hours));
                }else if(it.isEqual(endDate)){
                    float hours = computeDayElapsedHours(endTime);
                    hourOfDayList.add(new HourOfDay(it,hours));
                }else{
                    hourOfDayList.add(new HourOfDay(it,24));
                }
            }
        }

         return hourOfDayList;
    }

    /**
     *  计算起始时间和结算时间之间经过的日期列表
     * @param beginTime
     * @param endTime
     * @return
     */
    public static List<LocalDate> computeBetweenDays(LocalDateTime beginTime,LocalDateTime endTime){
        if(beginTime.isAfter(endTime)){
            throw new IllegalArgumentException("beginTime is after endTime,please check");
        }
        LocalDate beginDate = beginTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        List<LocalDate> localDateList = new ArrayList<>();

        LocalDate tempDate = beginDate;

        while(!tempDate.isAfter(endDate)){
            localDateList.add(tempDate);
            tempDate = tempDate.plusDays(ONE_DAY);
        }
        return localDateList;
    }

}
