package com.atzuche.order.commons.entity.dto;

import lombok.Data;

@Data
public class GetReturnCarCostReqDto {
    /**
     * 基本信息
     */
    private CostBaseDTO costBaseDTO;

    /**
     * 城市code
     */
    private Integer cityCode;

    /**
     * 订单来源
     */
    private Integer source;
    /**
     * 场景号
     */
    private String entryCode;
    /**
     * 取车经度
     */
    private String srvGetLon;
    /**
     * 取车纬度
     */
    private String srvGetLat;
    /**
     * 还车经度
     */
    private String srvReturnLon;
    /**
     * 还车纬度
     */
    private String srvReturnLat;
    /**
     * 车辆显示经度
     */
    private String carShowLon;
    /**
     * 车辆显示纬度
     */
    private String carShowLat;

    /**
     * 车辆真实经度
     */
    private String carRealLon;
    /**
     * 车辆真实纬度
     */
    private String carRealLat;


    /**
     * 是否套餐订单(true:是，false:否)
     */
    private Boolean IsPackageOrder;
    /**
     * 租金+保险+不计免赔+手续费（不需要传递）
     */
    private Integer sumJudgeFreeFee;
    /**
     * 是否计算取车费用
     */
    private Boolean isGetCarCost;
    /**
     * 是否计算还车费用
     */
    private Boolean isReturnCarCost;

}
