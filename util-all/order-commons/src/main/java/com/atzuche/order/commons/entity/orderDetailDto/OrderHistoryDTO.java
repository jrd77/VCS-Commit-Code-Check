package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OrderHistoryDTO {
    @AutoDocProperty("订单号")
    public String orderNo;
    @AutoDocProperty("订单类型")
    public String category;
    @AutoDocProperty("车主名称")
    public String ownerName;
    @AutoDocProperty("车主手机号码")
    public String ownerPhone;
    @AutoDocProperty("下单城市名称")
    public String cityName;
    @AutoDocProperty("下单城市编码")
    public String cityCode;
    @AutoDocProperty("下单地址")
    public String reqAdd;
    @AutoDocProperty("用车起始时间")
    public String rentTime;
    @AutoDocProperty("用车结束时间")
    public String revertTime;
    @AutoDocProperty("找车/取车地点")
    public String addr;
    @AutoDocProperty("品牌车型")
    public String carTypeTxt;
    @AutoDocProperty("车辆类型")
    public String carUseType;
    @AutoDocProperty("变速箱类型")
    public String carGearboxType;
    @AutoDocProperty("车辆状态")
    public String carStatus;
    @AutoDocProperty("日限里程")
    public Integer carDayMileage;

    @AutoDocProperty("订单总租金")
    public Integer rentTotalAmt;
    @AutoDocProperty("订单总保险")
    public Integer totalInsurance;
    @AutoDocProperty("日均价")
    public Integer avragePrice;

  /*  @AutoDocProperty("调度失败原因")
    public String dispatchFailReason;*/

    @AutoDocProperty(value="近10单成单率，存整数")
    public Integer sucessRate;

    @AutoDocProperty("车龄")
    public Integer carAge;

    @AutoDocProperty("牌照")
    public String isLocal;

    @AutoDocProperty("是否为精选车(是否神马车辆)")
    public String choiceCar;
}
