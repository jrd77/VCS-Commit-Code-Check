package com.atzuche.order.coreapi.entity.dto.cost;

import com.atzuche.order.coreapi.entity.dto.cost.req.*;
import lombok.Data;

/**
 * 费用计算参数封装
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:24
 */
@Data
public class OrderCostReqContext {

    /**
     * 基础参数
     */
    private OrderCostBaseReqDTO baseReqDTO;

    /**
     * 计算租金参数
     */
    private OrderCostRentAmtReqDTO rentAmtReqDTO;

    /**
     * 计算基础保险费参数
     */
    private OrderCostInsurAmtReqDTO insurAmtReqDTO;

    /**
     * 计算全面保障费参数
     */
    private OrderCostAbatementAmtReqDTO abatementAmtReqDTO;

    /**
     * 计算附加驾驶人保险费参数
     */
    private OrderCostExtraDriverReqDTO extraDriverReqDTO;

    /**
     * 计算取还车服务费参数
     */
    private OrderCostGetReturnCarCostReqDTO getReturnCarCostReqDTO;

    /**
     * 计算超运能溢价参数
     */
    private OrderCostGetReturnCarOverCostReqDTO getReturnCarOverCostReqDTO;

    /**
     * 计算长租订单抵扣租金参数
     */
    private LongOrderOwnerCouponReqDTO longOrderOwnerCouponReqDTO;

    /**
     * 计算送取服务券抵扣信息参数
     */
    private OrderCostGetCarFeeCouponReqDTO orderCostGetCarFeeCouponReqDTO;

    /**
     * 计算车主券抵扣信息参数
     */
    private OrderCostOwnerCouponReqDTO ownerCouponReqDTO;

    /**
     * 计算优惠券抵扣信息参数
     */
    private OrderCostPlatformCouponReqDTO costCouponReqDTO;

    /**
     * 计算限时红包抵扣信息参数
     */
    private OrderCostLimitRedReqDTO limitRedReqDTO;

    /**
     * 计算凹凸币抵扣信息参数
     */
    private OrderCostAutoCoinReqDTO autoCoinReqDTO;

    /**
     * 计算车辆押金参数
     */
    private OrderCostCarDepositAmtReqDTO costCarDepositAmtReqDTO;

    /**
     * 计算违章押金参数
     */
    private OrderCostViolationDepositAmtReqDTO violationDepositAmtReqDTO;

}
