package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(value= JsonInclude.Include.NON_EMPTY)
@Data
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

    @AutoDocProperty("GPS号")
    private String gps;
    @AutoDocProperty("SIM卡号")
    private String simNo;

    @AutoDocProperty("车辆备注")
    private String memo;
    @AutoDocProperty("车辆详细")
    private String carRemark;

    @AutoDocProperty("取还车说明")
    private String getRevertExplain;

    @AutoDocProperty("日限里程")
    private Integer dayMileage;
}
