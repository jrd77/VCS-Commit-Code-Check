package com.atzuche.order.coreapi.entity.request;

import com.atzuche.order.commons.entity.request.ItemList;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:14
 * @Description: 订单提交时的入参DTO
 * 
 **/
@Data
public class SubmitOrderReq {
    private String useSpecialPrice;
    private String srvGetAddr;
    private String srvReturnAddr;
    private Integer source;
    private String operator;
    private String useAutoCoin;
    private String srvGetLat;
    private Integer srvGetFlag;
    private Integer carNo;
    private String sceneCode;
    private String getCarFreeCouponId;
    private String disCouponIds;
    private String specialConsole;
    private Integer useBal;
    private String srvReturnLat;
    private String token;
    private String rentReason;
    private String srvReturnLon;
    private String offlineOrderStatus;
    private Integer srvReturnFlag;
    private LocalDateTime revertTime;
    private Integer oilType;
    private String conPhone;
    private LocalDateTime rentTime;
    private String srvGetLon;
    private Integer memNo;
    private Integer reqSource;
    private String reqOs;
    private Integer longitude;
    private Integer latitude;
    private String reqVersion;
    private Integer isLeaveCity;
    private String subSource;
    private String cityCode;
    private String requestId;
    private String useAirportService;
    private String schema;
    private String ModuleName;
    private String FunctionName;
    private String publicCityCode;
    private String OsVersion;
    private String limitRedStatus;
    private String AppVersion;
    private String OS;
    private String PublicLatitude;
    private String freeDoubleTypeId;
    private String rentCity;
    private String deviceName;
    private String publicToken;
    private String carAddrIndex;
    private String PublicLongitude;
    private List<ItemList> itemList;
    private String reqIp;
    private String mac;
    private String AppChannelId;
    private String appName;
    private String androidID;
}
