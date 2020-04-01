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
}
