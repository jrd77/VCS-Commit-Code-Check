package com.atzuche.order.coreapi.entity.dto.cost;

import com.atzuche.order.coreapi.entity.dto.cost.res.*;
import lombok.Data;

/**
 * 订单费用清单
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:25
 */
@Data
public class OrderCostResContext {

    /**
     * 租金信息
     */
    private OrderRentAmtResDTO orderRentAmtResDTO;

    /**
     * 基础保障费信息
     */
    private OrderInsurAmtResDTO orderInsurAmtResDTO;

    /**
     * 全面保障服务信息
     */
    private OrderAbatementAmtResDTO orderAbatementAmtResDTO;

    /**
     * 附加驾驶人保险费信息
     */
    private OrderExtraDriverInsureAmtResDTO orderExtraDriverInsureAmtResDTO;

    /**
     * 手续费信息
     */
    private OrderServiceChargeResDTO orderServiceChargeResDTO;

    /**
     * 取还车服务费信息
     */
    private OrderGetAndReturnCarCostResDTO orderGetAndReturnCarCostResDTO;

    /**
     * 超运能溢价信息
     */
    private OrderOverTransportCapacityPremiumResDTO orderOverTransportCapacityPremiumResDTO;

    /**
     * 基础保障费折扣信息
     */
    private OrderInsurAmtDeductionResDTO orderInsurAmtDeductionResDTO;

    /**
     * 全面保障服务折扣信息
     */
    private OrderAbatementAmtDeductionResDTO orderAbatementAmtDeductionResDTO;

    /**
     * 长租订单租金折扣及补贴信息
     */
    private LongOrderOwnerCouponResDTO longOrderOwnerCouponResDTO;

    /**
     * 长租订单取还车服务费补贴信息
     */
    private LongOrderGetAndReturnCarCostSubsidyResDTO longOrderGetAndReturnCarCostSubsidyResDTO;

    /**
     * 车主券抵扣及补贴信息
     */
    private OrderOwnerCouponResDTO orderOwnerCouponResDTO;

    /**
     * 送取服务券抵扣及补贴信息
     */
    private OrderGetCarFeeCouponResDTO orderGetCarFeeCouponResDTO;

    /**
     * 限时红包补贴信息
     */
    private OrderLimitRedResDTO orderLimitRedResDTO;

    /**
     * 平台优惠券抵扣及补贴信息
     */
    private OrderPlatformCouponResDTO orderPlatformCouponResDTO;

    /**
     * 凹凸币补贴信息
     */
    private OrderAutoCoinResDTO orderAutoCoinResDTO;

    /**
     * 车辆押金信息
     */
    private OrderCarDepositAmtResDTO orderCarDepositAmtResDTO;

    /**
     * 违章押金信息
     */
    private OrderIllegalDepositAmtResDTO orderIllegalDepositAmtResDTO;
}
