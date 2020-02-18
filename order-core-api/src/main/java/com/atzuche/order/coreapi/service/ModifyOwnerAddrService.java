package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.autoyol.cardistance.CarDistanceUtil;
import com.autoyol.platformcost.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOwnerAddrService {
	@Autowired
    private CityConfigSDK cityConfigSDK;
    @Autowired
    private SysConfigSDK sysConfigSDK;
    // 修改地址距离原地址>2公里需要收费
    private static final double MODIFY_ADDR_NEED_CHARGE_DISTANCE = 1.0;
    
    
    
    /**
     * 获取提前小时数
     * @param cityCode
     * @return Integer
     */
    private Integer getBeforeTransTimeSpan(Integer cityCode) {
    	log.info("config-从城市配置中获取beforeTransTimeSpan,cityCode=[{}]", cityCode);
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(),cityCode);
        log.info("config-从城市配置中获取beforeTransTimeSpan,configByCityCode=[{}]", JSON.toJSONString(configByCityCode));
        Integer beforeTransTimeSpan = configByCityCode.getBeforeTransTimeSpan()==null?CommonUtils.BEFORE_TRANS_TIME_SPAN:configByCityCode.getBeforeTransTimeSpan();
        return beforeTransTimeSpan;
    }
	
    /**
     * 获取修改取车违约金
     * @return Integer
     */
	private Integer getGetFineAmt() {
		List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
		List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_FINE_AMT.equals(x.getAppType()))
                .collect(Collectors.toList());
		SysConfigEntity sysGetFineAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.GET_FINE_AMT.equals(x.getItemKey())).findFirst().get();
        log.info("config-从配置中获取取车违约金getFineAmt=[{}]",JSON.toJSONString(sysGetFineAmt));
        Integer getCost = (sysGetFineAmt==null||sysGetFineAmt.getItemValue()==null) ? CommonUtils.MODIFY_GET_FINE_AMT : Integer.valueOf(sysGetFineAmt.getItemValue());
        return getCost;
        
	}
	
	/**
	 * 获取修改还车违约金
	 * @return Integer
	 */
	private Integer getReturnFineAmt() {
		List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
		List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_FINE_AMT.equals(x.getAppType()))
                .collect(Collectors.toList());

        SysConfigEntity sysReturnFineAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.RETURN_FINE_AMT.equals(x.getItemKey())).findFirst().get();
		log.info("config-从配置中获取还车违约金returnFineAmt=[{}]",JSON.toJSONString(sysReturnFineAmt));
        Integer returnCost = (sysReturnFineAmt==null||sysReturnFineAmt.getItemValue()==null) ? CommonUtils.MODIFY_RETURN_FINE_AMT : Integer.valueOf(sysReturnFineAmt.getItemValue());
        return returnCost;
	}
	
	
	/**
     * 计算是否未修改地址
     *
     * @param currentAddress
     * @param serviceAddress
     * @return
     */
    private boolean isCurrentAddress(String currentAddress, String serviceAddress) {
        return StringUtils.isEmpty(serviceAddress) || currentAddress.equalsIgnoreCase(serviceAddress);
    }

	/**
     * 是否可以修改该段取还车地址
     *
     * @param localDateTime
     * @param serviceFlag
     * @return boolean
     */
    private boolean isAllowModify(LocalDateTime localDateTime, LocalDateTime now, Integer serviceFlag) {
        if (serviceFlag == 1 && now.isBefore(localDateTime)) {
            // 只有在使用的取还车服务开关,并且当前时间在取还车时间之前才可以修改
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param localDateTime
     * @param now
     * @param serviceFlag
     * @param orderStatus
     * @return
     */
    private boolean isAllowModify(LocalDateTime localDateTime, LocalDateTime now, Integer serviceFlag,Integer orderStatus) {
        if (serviceFlag == 1 && 
        		(now.isBefore(localDateTime) || 
        				(orderStatus != null && orderStatus.intValue() == OrderStatusEnum.TO_RETURN_CAR.getStatus())) ) {  //now.isBefore(localDateTime)  当前时间小于还车时间
            // 只有在使用的取还车服务开关,并且当前时间在取还车时间之前才可以修改
            return true;
        }
        return false;
    }
    
    /**
     * 计算本次修改是否需要收费
     *
     * @param localDateTime
     * @param now
     * @param serviceLon
     * @param serviceLat
     * @return
     */
    private boolean isNeedCharge(LocalDateTime localDateTime, LocalDateTime now, Integer beforeHour,
                                 String serviceLon, String serviceLat, String currentServiceLon, String currentServiceLat,Integer orderStatus) {
        return this.isTimeNeedCharge(localDateTime, now, beforeHour, orderStatus)
                && this.isAddressNeedCharge(serviceLon, serviceLat, currentServiceLon, currentServiceLat);
    }
    
    /**
     * 时间是否命中收费规则,4小时前免费，4小时内收费。
     *
     * @param localDateTime
     * @param now
     * @param beforeHour
     * @return
     */
    private boolean isTimeNeedCharge(LocalDateTime localDateTime, LocalDateTime now, Integer beforeHour, Integer orderStatus) {
        return now.isAfter(localDateTime.minusHours(beforeHour)) || 
        		(now.isAfter(localDateTime) && orderStatus != null && 
        		orderStatus.intValue() == OrderStatusEnum.TO_RETURN_CAR.getStatus()) ;
    }
    
    /**
     * 地址是否命中收费规则
     * @param serviceLon
     * @param serviceLat
     * @param currentServiceLon
     * @param currentServiceLat
     * @return
     */
    private boolean isAddressNeedCharge(String serviceLon, String serviceLat, String currentServiceLon, String currentServiceLat) {
        if (StringUtils.isNotEmpty(serviceLon) && StringUtils.isNotEmpty(serviceLat)) {
            double realDistance = CarDistanceUtil.getDistance(NumberUtils.toDouble(serviceLat), NumberUtils.toDouble(serviceLon), 
            		NumberUtils.toDouble(currentServiceLat), NumberUtils.toDouble(currentServiceLon));
            
            log.info("calc owner modify address is need charge,realDistance={}", realDistance);
            return realDistance > MODIFY_ADDR_NEED_CHARGE_DISTANCE;
        }

        return false;
    }
}
