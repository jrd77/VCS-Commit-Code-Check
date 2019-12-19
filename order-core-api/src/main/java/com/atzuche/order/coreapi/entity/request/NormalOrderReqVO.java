package com.atzuche.order.coreapi.entity.request;

import com.atzuche.order.commons.entity.request.ItemList;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:14
 * @Description: 订单提交时的入参DTO
 * 
 **/
@Data
public class NormalOrderReqVO implements Serializable {

    /**
     * 是否使用凹凸币 0-代表使用 1-代表使用
     */
    private Integer useAutoCoin;
    /**
     * 是否使用钱包余额,0-代表不使用，1-代表使用
     */
    private Integer useBal;
    /**
     * 取车地址
     */
    private String srvGetAddr;
    private String srvGetLat;
    private String srvGetLon;
    /**
     * 是否使用取车标识，0-代表没有，1-代表有
     */
    private Integer srvGetFlag;
    /**
     * 还车地址
     */
    private String srvReturnAddr;
    private String srvReturnLat;
    private String srvReturnLon;
    /**
     * 是否使用还车服务标识
     */
    private Integer srvReturnFlag;

    /**
     * 车辆号
     */
    private String carNo;

    private String getCarFreeCouponId;
    private String disCouponIds;
    private String carOwnerCouponNo;

    private Integer abatement;



    private LocalDateTime revertTime;
    private LocalDateTime rentTime;

    private String memNo;




    /**
     * 用车城市编码，例如上海310100
     */
    private String cityCode;

    /**
     * 是否使用机场服务
     */
    private Integer useAirportService;
    /**
     * 选择机场服务时要搭乘的航班号
     */
    private String flightNo;


    /**
     * 是否使用限时红包
     */
    private String limitRedStatus;
    private String limitReductionId;


    /**
     * 免押方式ID：1.绑卡免押，2.芝麻免押，3.支付押金
     */
    private String freeDoubleTypeId;

    /**
     * 常用驾驶人ID,非必填,多个以逗号分隔
     */
    private String driverIds;

    /**
     * 车辆地址的序号，默认为0
     */
    private String carAddrIndex;


    /**
     * 风控参数
     */
    private Integer isLeaveCity;
    /**
     * 如果isLeaveCity=1，该字段代表去往的城市，否则就是本市
     */
    private String rentCity;

    //统计数据
    private Integer reqSource;
    private Integer source;
    private String sceneCode;
    private String mac;
    private String AppChannelId;
    private String appName;
    private String androidID;
    private String deviceName;
    private String OsVersion;
    private String ModuleName;
    private String FunctionName;
    private String reqVersion;
    private String reqOs;
    private String requestId;
    private String OAID;
    private String queryId;
    private String IMEI;
    private String AppVersion;
    private String OS;
    private String subSource;
    private String srcIp;
    private Integer srcPort;
    /**
     * 查车的模式，用于统计
     */
    private String schema;

    private String rentReason;

    private String PublicLongitude;
    private String PublicLatitude;
    private String publicCityCode;

    private String utmSource;
    private String utmMedium;
    private String utmTerm;
    private String utmCampaign;



}
