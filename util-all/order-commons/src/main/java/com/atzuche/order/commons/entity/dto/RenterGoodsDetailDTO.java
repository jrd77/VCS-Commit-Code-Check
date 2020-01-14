package com.atzuche.order.commons.entity.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RenterGoodsDetailDTO {
    /**
     * 起租日期
     */
    private LocalDateTime rentTime;
    /**
     * 还车日期
     */
    private LocalDateTime revertTime;

    /**
     * 老订单的起租日期
     */
    private LocalDateTime oldRentTime;

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
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
     * 车辆指导价
     */
    private Integer carGuidePrice;
    /**
     * 保费计算用购置价
     */
    private Integer carInmsrp;
    /**
     * 车辆标签
     */
    private String carTag;
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
     */
    private Integer carOwnerType;
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
     * 停运费比例
     */
    private Double stopCostRate;
    /**
     * 动力源：1-纯电动，2-汽油，3-油电混动，4-柴油，5-天然气，6-石油气
     */
    private Integer engineSource;
    /**
     * 车架号
     */
    private String frameNo;
    /**
     * 发动机号
     */
    private String engineNum;
    /**
     * 平台服务费比例/代管车服务费比例（仅车主端有）
     */
    private Double serviceRate;

    /**
     * 一天一价
     */
    List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;

    /**
     * 老标签id
     */
    private List<String> labelIds;
    /**
     * 车主会员号
     */
    private String ownerMemNo;

    /**
     * 车辆品牌
     */
    private String brand;
    /**
     * 车型
     */
    private String type;

    /**
     * 行驶证注册年月
     */
    private LocalDate licenseDay;
    /**
     * 车辆日均指导价
     */
    private Integer carGuideDayPrice;
    /**
     * 油表总刻度
     */
    private Integer oilTotalCalibration;
    /**
     * GPS序号
     */
    private String gpsSerialNumber;
    /**
     * 年份
     */
    private String year;

    /**
     * 是否超证:0否 1是 (车主端用)
     */
    private Integer moreLicenseFlag;
    /**
     * 行驶证到期日期(车主端用)
     */
    private LocalDateTime licenseExpire;
    /**
     * 年检到期日期
     */
    private LocalDate inspectExpire;

}
