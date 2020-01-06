package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 优惠券抵扣
 */
@Data
@ToString
public class CouponDeductionVO {

    @AutoDocProperty("车主券")
    private String ownerCoupon;
    @AutoDocProperty("平台券")
    private String platFormCoupon;
    @AutoDocProperty("送取服务券")
    private String deliveryCoupon;
    @AutoDocProperty("钱包余额")
    private Integer walletBalance;
    @AutoDocProperty("凹凸币")
    private String coin;
    @AutoDocProperty("车主券实际抵扣金额")
    private String ownerCouponRealDeduction;
    @AutoDocProperty("平台券实际抵扣金额")
    private String platFormCouponRealDeduction;
    @AutoDocProperty("送取服务券实际抵扣金额")
    private String deliveryCouponRealDeduction;
    @AutoDocProperty("钱包余额实际抵扣金额")
    private Integer walletBalanceRealDeduction;
    @AutoDocProperty("凹凸币实际抵扣金额")
    private String coinRealDeduction;
    @AutoDocProperty("子订单编号")
    private String renterOrderNo;

}
