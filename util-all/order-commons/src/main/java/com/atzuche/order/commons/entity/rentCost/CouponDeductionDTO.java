package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponDeductionDTO {

    @AutoDocProperty("车主优惠券")
    public Integer ownerCouponRealDeduction = 0;
    @AutoDocProperty("平台优惠券")
    public Integer platFormCouponRealDeduction = 0;
    @AutoDocProperty("送取服务券")
    public Integer deliveryCouponRealDeduction = 0;
    @AutoDocProperty("钱包余额")
    public Integer walletBalanceRealDeduction = 0;
    @AutoDocProperty(value = "凹凸币")
    public Integer autoCoinSubsidyAmt;

    @AutoDocProperty(value="车主长租折扣")
    public String ownerLongRentDeduct;
    @AutoDocProperty(value="车主长租折扣实际抵扣金额")
    public Integer ownerLongRentDeductAmt;

}
