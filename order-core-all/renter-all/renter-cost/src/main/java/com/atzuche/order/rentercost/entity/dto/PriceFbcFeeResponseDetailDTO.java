package com.atzuche.order.rentercost.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class PriceFbcFeeResponseDetailDTO {
    @AutoDocProperty("取还类型:get/return")
    private String getReturnType;
    @AutoDocProperty("预期应收费用")
    private String expectedShouldFee;
    @AutoDocProperty("预期实收费用")
    private String expectedRealFee;
    @AutoDocProperty("基础取还车费用")
    private String baseFee;
    @AutoDocProperty("时间段溢价")
    private String timePeriodUpPrice;
    @AutoDocProperty("圈层溢价")
    private String cicrleUpPrice;
    @AutoDocProperty("远途溢价")
    private String distanceUpPrice;
    @AutoDocProperty("渠道系数")
    private String channelCoefficient;
    @AutoDocProperty("实际放大后的用于计算的距离")
    private String showDistance;
}
