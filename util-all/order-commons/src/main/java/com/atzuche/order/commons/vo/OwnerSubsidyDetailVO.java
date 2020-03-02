package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerSubsidyDetailVO {
    @AutoDocProperty(value= "车主给租客的租金补贴金额")
    private Integer subsidyOwnerTorenterRentamt;

    @AutoDocProperty(value="车主物品损失补贴")
    private Integer ownerGoodsSubsidy;

    @AutoDocProperty(value="车主延时补贴")
    private Integer ownerDelaySubsidy;

    @AutoDocProperty(value="车主交通费补贴")
    private Integer ownerTrafficSubsidy;

    @AutoDocProperty(value="车主收益补贴")
    private Integer ownerIncomeSubsidy;

    @AutoDocProperty(value="车主洗车补贴")
    private Integer ownerWashCarSubsidy;

    @AutoDocProperty(value="其他补贴")
    private Integer ownerOtherSubsidy;

    @AutoDocProperty(value = "车主券抵扣金额")
    private Integer ownerCouponOffsetCost;

    @AutoDocProperty(value = "优惠券抵扣金额")
    private Integer realCouponOffset;
}
