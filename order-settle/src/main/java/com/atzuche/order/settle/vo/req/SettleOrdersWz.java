package com.atzuche.order.settle.vo.req;

import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import lombok.Data;

/**
 * 订单违章结算对象
 */
@Data
public class SettleOrdersWz {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;
    /**
     * 租客会员号
     */
    private String renterMemNo;

    /**
     * 违章费用
     */
    private int renterOrderCostWz;

    /**
     * 租客费用明细
     */
    private RentCostsWz rentCostsWz;

    /**
     *  租客订单信息
     */
    RenterOrderEntity renterOrder;

}
