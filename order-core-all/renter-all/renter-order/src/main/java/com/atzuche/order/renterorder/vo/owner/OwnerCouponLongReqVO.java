package com.atzuche.order.renterorder.vo.owner;

import lombok.Data;

/**
 * 长租折扣券参数
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:02
 */
@Data
public class OwnerCouponLongReqVO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 租客会员号
     */
    private String renterMemNo;

    /**
     * 车主会员号
     */
    private String ownerMemNo;

    /**
     * 车辆注册号
     */
    private String carNo;

    /**
     * 原始租金单价
     */
    private Integer rentOriginalUnitPriceAmt;

    /**
     * 订单租期开始时间
     */
    private String rentTime;

    /**
     * 订单租期截至时间
     */
    private String revertTime;

    /**
     * 车主券编码
     */
    private String couponCode;
}
