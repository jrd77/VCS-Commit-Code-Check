package com.atzuche.order.renterorder.vo.owner;

import lombok.Data;

/**
 * 长租折扣券信息
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:03
 */
@Data
public class OwnerCouponLongResVO {

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 原始租车单价金额
     */
    private Integer rentOriginalUnitPriceAmt;

    /**
     * 抵扣比例
     */
    private Double discounRatio;

    /**
     * 减免金额
     */
    private Integer reductionAmt;

    /**
     * 实际租金单价
     */
    private Integer actRentUnitPriceAmt;

}
