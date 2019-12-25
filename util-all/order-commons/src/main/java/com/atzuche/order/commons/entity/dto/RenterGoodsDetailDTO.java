package com.atzuche.order.commons.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RenterGoodsDetailDTO {
    private String renterOrderNo;
    private String orderNo;
    private LocalDateTime rentTime;
    private LocalDateTime revertTime;

    /**
     * 应答标识位，0未设置，1已设置
     */
    private Integer replyFlag;
    /**
     * 车辆号
     */
    private Integer carNo;
    /**
     * 车牌号
     */
    private String carPlateNum;
    /**
     * 品牌ID
     */
    private Integer carBrand;
    /**
     * 品牌名称
     */
    private String carBrandTxt;
    /**
     * 车辆等级
     */
    private Integer carRating;
    /**
     * 车型Id
     */
    private Integer carType;
    /**
     * 车型名称
     */
    private String carTypeTxt;
    /**
     * 排量
     */
    private Double carCylinderCapacity;
    /**
     * L/T
     */
    private String carCcUnit;

    /**
     * 变速箱类型
     */
    private Integer carGearboxType;
    /**
     * 日限里程
     */
    private Integer carDayMileage;
    /**
     * 商品简介
     */
    private String carIntrod;
    /**
     * 车辆残值
     */
    private Integer carSurplusPrice;
    /**
     * 是否使用特供价 0-否，1-是
     */
    private boolean carUseSpecialPrice;
    /**
     * 车辆指导价
     */
    private Integer carGuidePrice;
    /**
     * 车辆状态
     */
    private Integer carStatus;
    /**
     * 车辆图片地址
     */
    private String carImageUrl;
    /**
     * 车主类型:5个人车主、10租赁公司、15其他 、20托管车辆-交易用、25托管车辆-工作用、30短期托管车、35代管车辆
     * （车主端用）
     */
    private Integer carOwnerType;
    /**
     * 车主会员号
     * （车主端用）
     */
    private String ownerMemNo;

    /**
     * 功能类型 1:MPV，2：SUV,3:中型车，4：中大型车，5：其它，6：客车，7：小型车，8：微型车，9：房车，10：皮卡，11：紧凑型车，12：豪华车，13：跑车，14：面包车
     */
    private Integer carUseType;
    /**
     * 油箱容量
     */
    private Integer carOilVolume;
    /**
     * 燃料类型 1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油
     */
    private Integer carEngineType;
    /**
     * 车辆描述
     */
    private String carDesc;
    /**
     * 车管家电话号码
     */
    private String carStewardPhone;
    /**
     * 车辆检测状态
     */
    private Integer carCheckStatus;
    /**
     * 车辆显示地址
     */
    private String carShowAddr;
    /**
     * 车辆显示经度
     */
    private String carShowLon;
    /**
     * 车辆显示纬度
     */
    private String carShowLat;
    /**
     * 真实地址
     */
    private String carRealAddr;
    /**
     * 车辆真实经度
     */
    private String carRealLon;
    /**
     * 车辆真实纬度
     */
    private String carRealLat;
    /**
     * 选择的车辆地址序号
     */
    private Integer carAddrIndex;


    /**
     * 一天一价
     */
    List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;



}
