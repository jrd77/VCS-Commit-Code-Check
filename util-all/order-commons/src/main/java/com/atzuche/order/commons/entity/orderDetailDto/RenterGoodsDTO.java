package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 租客商品概览表
 * 
 * @author ZhangBin
 * @date 2020-01-14 16:45:27
 * @Description:
 */
@Data
public class RenterGoodsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        @AutoDocProperty(value="主订单号",required=true)
        private String orderNo;
    	/**
         * 子订单号
         */
        @AutoDocProperty(value="子订单号",required=true)
        private String renterOrderNo;
    	/**
         * 应答标识位，0未设置，1已设置
         */
        @AutoDocProperty(value="应答标识位，0未设置，1已设置",required=true)
        private Integer replyFlag;
    	/**
         * 车辆号
         */
        @AutoDocProperty(value="车辆号",required=true)
        private Integer carNo;
    	/**
         * 车牌号
         */
        @AutoDocProperty(value="车牌号",required=true)
        private String carPlateNum;
    	/**
         * 品牌ID
         */
        @AutoDocProperty(value="品牌ID",required=true)
        private Integer carBrand;
    	/**
         * 品牌名称
         */
        @AutoDocProperty(value="品牌名称",required=true)
        private String carBrandTxt;
    	/**
         * 车辆等级
         */
        @AutoDocProperty(value="车辆等级",required=true)
        private Integer carRating;
    	/**
         * 车型Id
         */
        @AutoDocProperty(value="车型Id",required=true)
        private Integer carType;
    	/**
         * 车型名称
         */
        @AutoDocProperty(value="车型名称",required=true)
        private String carTypeTxt;
    	/**
         * 排量
         */
        @AutoDocProperty(value="排量",required=true)
        private Double carCylinderCapacity;
    	/**
         * L/T
         */
        @AutoDocProperty(value="L/T",required=true)
        private String carCcUnit;
    	/**
         * 变速箱类型
         */
        @AutoDocProperty(value="变速箱类型",required=true)
        private Integer carGearboxType;
    	/**
         * 日限里程
         */
        @AutoDocProperty(value="日限里程",required=true)
        private Integer carDayMileage;
    	/**
         * 商品简介
         */
        @AutoDocProperty(value="商品简介",required=true)
        private String carIntrod;
    	/**
         * 车辆残值
         */
        @AutoDocProperty(value="车辆残值",required=true)
        private Integer carSurplusPrice;
    	/**
         * 车辆指导价
         */
        @AutoDocProperty(value="车辆指导价",required=true)
        private Integer carGuidePrice;
    	/**
         * 保费计算用购置价
         */
        @AutoDocProperty(value="保费计算用购置价",required=true)
        private Integer carInmsrp;
    	/**
         * 车辆标签
         */
        @AutoDocProperty(value="车辆标签",required=true)
        private String carTag;
    	/**
         * 车辆状态
         */
        @AutoDocProperty(value="车辆状态",required=true)
        private Integer carStatus;
    	/**
         * 车辆图片地址
         */
        @AutoDocProperty(value="车辆图片地址",required=true)
        private String carImageUrl;
    	/**
         * 车主类型:5个人车主、10租赁公司、15其他 、20托管车辆-交易用、25托管车辆-工作用、30短期托管车、35代管车辆
         */
        @AutoDocProperty(value="车主类型:5个人车主、10租赁公司、15其他 、20托管车辆-交易用、25托管车辆-工作用、30短期托管车、35代管车辆",required=true)
        private Integer carOwnerType;
    	/**
         * 功能类型 1:MPV，2：SUV,3:中型车，4：中大型车，5：其它，6：客车，7：小型车，8：微型车，9：房车，10：皮卡，11：紧凑型车，12：豪华车，13：跑车，14：面包车 
         */
        @AutoDocProperty(value="功能类型 1:MPV，2：SUV,3:中型车，4：中大型车，5：其它，6：客车，7：小型车，8：微型车，9：房车，10：皮卡，11：紧凑型车，12：豪华车，13：跑车，14：面包车 ",required=true)
        private Integer carUseType;
    	/**
         * 油箱容量
         */
        @AutoDocProperty(value="油箱容量",required=true)
        private Integer carOilVolume;
    	/**
         * 燃料类型 1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油       
         */
        @AutoDocProperty(value="燃料类型 1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油       ",required=true)
        private Integer carEngineType;
    	/**
         * 车辆描述
         */
        @AutoDocProperty(value="车辆描述",required=true)
        private String carDesc;
    	/**
         * 车管家电话号码
         */
        @AutoDocProperty(value="车管家电话号码",required=true)
        private String carStewardPhone;
    	/**
         * 车辆检测状态
         */
        @AutoDocProperty(value="车辆检测状态",required=true)
        private Integer carCheckStatus;
    	/**
         * 车辆显示地址
         */
        @AutoDocProperty(value="车辆显示地址",required=true)
        private String carShowAddr;
    	/**
         * 车辆显示经度
         */
        @AutoDocProperty(value="车辆显示经度",required=true)
        private String carShowLon;
    	/**
         * 车辆显示纬度
         */
        @AutoDocProperty(value="车辆显示纬度",required=true)
        private String carShowLat;
    	/**
         * 真实地址
         */
        @AutoDocProperty(value="真实地址",required=true)
        private String carRealAddr;
    	/**
         * 车辆真实经度
         */
        @AutoDocProperty(value="车辆真实经度",required=true)
        private String carRealLon;
    	/**
         * 车辆真实纬度
         */
        @AutoDocProperty(value="车辆真实纬度",required=true)
        private String carRealLat;
    	/**
         * 油表总刻度
         */
        @AutoDocProperty(value="油表总刻度",required=true)
        private Integer oilTotalCalibration;
    	/**
         * GPS序号
         */
        @AutoDocProperty(value="GPS序号",required=true)
        private String gpsSerialNumber;
    	/**
         * 选择的车辆地址序号
         */
        @AutoDocProperty(value="选择的车辆地址序号",required=true)
        private Integer carAddrIndex;
    	/**
         * 车辆日均指导价
         */
        @AutoDocProperty(value="车辆日均指导价",required=true)
        private Integer carGuideDayPrice;
    	/**
         * 停运费比例
         */
        @AutoDocProperty(value="停运费比例",required=true)
        private Double stopCostRate;
    	/**
         * 动力源：1-纯电动，2-汽油，3-油电混动，4-柴油，5-天然气，6-石油气
         */
        @AutoDocProperty(value="动力源：1-纯电动，2-汽油，3-油电混动，4-柴油，5-天然气，6-石油气",required=true)
        private Integer engineSource;
    	/**
         * 车架号
         */
        @AutoDocProperty(value="车架号",required=true)
        private String frameNo;
    	/**
         * 发动机号
         */
        @AutoDocProperty(value="发动机号",required=true)
        private String engineNum;
    	/**
         * 年份
         */
        @AutoDocProperty(value="年份",required=true)
        private String year;

        @AutoDocProperty(value="最新公里数")
        private Integer lastMileage;
    					
}
