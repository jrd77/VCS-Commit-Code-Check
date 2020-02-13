package com.atzuche.order.commons.vo.res.coupon;


import com.autoyol.doc.annotation.AutoDocProperty;

public class DisCoupon {

    @AutoDocProperty(value="优惠券id")
    private String disCouponId;

    @AutoDocProperty(value="优惠券标题")
    private String disName;

    @AutoDocProperty(value="满(元)")
    private String condAmt;

    @AutoDocProperty(value="减(元)")
    private String disAmt;

    @AutoDocProperty(value="开始时间(yyyyMMddHHmmss)")
    private String startDate;

    @AutoDocProperty(value="结束时间(yyyyMMddHHmmss)")
    private String endDate;

    @AutoDocProperty(value="实际抵扣金额")
    private String realCouponOffset;
}
