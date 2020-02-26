package com.atzuche.order.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/13 7:13 下午
 **/
@Data
@ToString
public class CarDetailDTO {
    @AutoDocProperty("车辆号")
    private Integer carNo;
    // 车牌号
    @AutoDocProperty("车牌号")
    private String plateNum;
    // 车牌号
    @AutoDocProperty("车牌号")
    private Integer city;
    // 经度
    @AutoDocProperty("经度")
    private Double cityLon;
    // 纬度
    @AutoDocProperty("纬度")
    private Double cityLat;
    // 城市名称
    @AutoDocProperty("城市名称")
    private String cityName;

    // 车主编号
    @AutoDocProperty("车主编号")
    private Integer ownerNo;
    // 车辆gps信息
    @AutoDocProperty("车辆gps信息")
    private String gps;

    // 品牌
    @AutoDocProperty("品牌")
    private String brandTxt;
    // 车型
    @AutoDocProperty("车型")
    private String typeTxt;
    // 车型id
    @AutoDocProperty("车型id")
    private String type;
    // 年款
    @AutoDocProperty(" 年款")
    private Integer year;
    // 型号
    @AutoDocProperty("型号")
    private String modelTxt;
    // 颜色
    @AutoDocProperty("颜色")
    private String color;
    @AutoDocProperty("颜色文本")
    private String colorStr;

    @AutoDocProperty("车辆指导价")
    private Integer guidePrice;

    @AutoDocProperty("驱动方式，1：两驱，2: 四驱")
    private Integer driveType;
    @AutoDocProperty("驱动方式文字释义")
    private String driveTypeTxt;

    @AutoDocProperty("车辆等级,1:A,2:B,3:C,4:D,5:D+,6:SUV A,7:SUV B,8:SUV C,9:SUV D,10:SUV D+,11:MPV")
    private Integer carLevel;

    @AutoDocProperty("发动机号")
    private String engineNum;

    @AutoDocProperty("车辆残值")
    private Integer surplusPrice;

    @AutoDocProperty("车架号（VIN码）")
    private String frameNo;
    @AutoDocProperty("行驶证所有人")
    private String licenseOwer;
    @AutoDocProperty("行驶证品牌车型")
    private String licenseModel;
    @AutoDocProperty("行驶证注册年月")
    private String licenseDay;
    @AutoDocProperty("行驶证到期日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String licenseExpire;
    @AutoDocProperty("保险到期日期（交强险）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date insuranceExpire;
    @AutoDocProperty("取还车说明")
    private String getRevertExplain;
    @AutoDocProperty("车辆备注")
    private String memo;

    // 日限里程
    @AutoDocProperty("日限里程")
    private Integer dayMileage;

    // 取车地址
    @AutoDocProperty("取车地址")
    private String getCarAddr;
    @AutoDocProperty("车辆详细")
    private String carDesc;

    @AutoDocProperty("车辆等级")
    private Integer rating;

    @AutoDocProperty("车辆使用类型（子类型）")
    private Integer ownerType;
    @AutoDocProperty("车辆使用类型文本释义（子类型）")
    private String ownerTypeTxt;

    @AutoDocProperty("SIM卡号")
    private String simNo;

    @AutoDocProperty("功能类型，1:MPV，2：SUV,3:中型车，4：中大型车，5：其它，6：客车，7：小型车，8：微型车，9：房车，10：皮卡，11：紧凑型车，12：豪华车，13：跑车，14：面包车")
    private Integer useType;
    @AutoDocProperty("功能类型文字释义")
    private String useTypeTxt;

}
