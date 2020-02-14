package com.atzuche.order.settle.vo.res;

import lombok.Data;

/**
 * 租客租车费用  押金  违章押金  信息
 */
@Data
public class RenterCostVO {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 应退 租车费用
     */
    private int renterCost;
    /**
     * 实退 租车费用
     */
    private int renterCostReal;

    /**
     * 实退押金
     */
    private int depositCostReal;
    /**
     * 应付退押金
     */
    private int depositCost;

    /**
     * 实退违章押金
     */
    private int depositWzCostReal;
    /**
     * 应付退违章押金
     */
    private int depositWzCost;
}
