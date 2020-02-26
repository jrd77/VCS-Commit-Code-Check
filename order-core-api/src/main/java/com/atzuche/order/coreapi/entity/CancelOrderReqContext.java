package com.atzuche.order.coreapi.entity;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import lombok.Data;

/**
 * 取消订单公共参数聚合
 *
 * @author pengcheng.fu
 * @date 2020/2/25 17:02
 */

@Data
public class CancelOrderReqContext {

    /**
     * 请求参数
     */
    private CancelOrderReqDTO cancelOrderReqDTO;

    /**
     * 主订单信息
     */
    private OrderEntity orderEntity;

    /**
     * 订单状态信息
     */
    private OrderStatusEntity orderStatusEntity;

    /**
     * 订单车主券信息
     */
    private OrderCouponEntity ownerCouponEntity;

    /**
     * 租客订单信息
     */
    private RenterOrderEntity renterOrderEntity;

    /**
     * 租客订单商品信息
     */
    private RenterGoodsDetailDTO renterGoodsDetailDTO;

    /**
     * 租客订单租车费用明细信息
     */
    private RenterOrderCostEntity renterOrderCostEntity;

    /**
     * 车主订单信息
     */
    private OwnerOrderEntity ownerOrderEntity;

    /**
     * 车主订单商品信息
     */
    private OwnerGoodsDetailDTO ownerGoodsDetailDTO;

    /**
     * 订单取消信息
     */
    private OrderCancelReasonEntity orderCancelReasonEntity;



}
