package com.atzuche.order.settle.vo.req;

import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import lombok.Data;

/**
 * 订单结算对象
 */
@Data
public class SettleOrders {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;
    /**
     * 车主订单号
     */
    private String ownerOrderNo;

    /**
     * 租客会员号
     */
    private String renterMemNo;

    /**
     * 车主会员号
     */
    private String ownerMemNo;

    /**
     * 租车费用
     */
    private int renterOrderCost;

    /**
     * 租客费用明细
     */
    private RentCosts rentCosts;

    /**
     * 车主费用明细
     */
    private OwnerCosts ownerCosts;

    /**
     *  租客订单信息
     */
    RenterOrderEntity renterOrder;
    /**
     * 车主订单信息
     */
    OwnerOrderEntity ownerOrder;

}
