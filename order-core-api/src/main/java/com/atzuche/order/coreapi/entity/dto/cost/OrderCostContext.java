package com.atzuche.order.coreapi.entity.dto.cost;

import lombok.Data;

/**
 * 订单费用计算参数与结果信息
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:22
 */
@Data
public class OrderCostContext {

    /**
     * 参数列表
     */
    private OrderCostReqContext reqContext;

    /**
     * 费用清单
     */
    private OrderCostResContext resContext;

    /**
     * 订单费用明细
     */
    private OrderCostDetailContext costDetailContext;

}
