package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.Date;

public class CarBusinessResVO {
    @AutoDocProperty("取车地址")
    private String getCarAddr;

    @AutoDocProperty("行驶证所有人")
    private String licenseOwer;

    @AutoDocProperty("行驶证品牌车型")
    private String licenseModel;

    @AutoDocProperty("行驶证到期日")
    private String licenseExpire;
    @AutoDocProperty("保险到期日期（交强险）")
    private String insuranceExpireDateStr;

    @AutoDocProperty("车辆gps信息")
    private Integer gps;

    @AutoDocProperty("车辆备注")
    private String memo;
    @AutoDocProperty("车辆详情")
    private String carRemark;

    @AutoDocProperty("取还车说明")
    private String getRevertExplain;

    @AutoDocProperty("日限里程")
    private Integer dayMileage;
}
