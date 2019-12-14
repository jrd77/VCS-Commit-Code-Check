package com.atzuche.order.coreapi.entity.request;

import com.atzuche.order.coreapi.entity.dto.ItemList;
import com.atzuche.order.coreapi.entity.dto.MemBaseInfoVo;
import com.atzuche.order.coreapi.entity.dto.MemInfoCache;
import lombok.Data;

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
    private Long revertTime;
    private Integer oilType;
    private String conPhone;
    private Long rentTime;
    private String srvGetLon;
    private String abatement;
    private MemInfoCache memInfoCache;
    private MemBaseInfoVo tokenMemInfo;
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
    private String IMEI;
    private String OsVersion;
    private String limitRedStatus;
    private String AppVersion;
    private String OS;
    private String PublicLatitude;
    private String mem_no;
    private String freeDoubleTypeId;
    private String rentCity;
    private String deviceName;
    private String publicToken;
    private String carAddrIndex;
    private String PublicLongitude;
    private String queryId;
    private List<ItemList> itemList;
    private String reqIp;
    private String mac;
    private String AppChannelId;
    private String appName;
    private String AndroidId;
    private String androidID;
}
