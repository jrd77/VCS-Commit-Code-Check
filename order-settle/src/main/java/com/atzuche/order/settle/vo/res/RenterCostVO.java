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
     * 应收租车费用
     */
    private int renterCostYingshou;
    /**
     * 实收租车费用
     */
    private int renterCostShishou;
    /**
     * 补付租车费用应付
     */
    private int renterCostBufu;

    /**
     * 补付租车费用实付
     */
    private int renterCostBufuShifu;

    /**
     * 实退押金
     */
    private int depositCostReal;
    /**
     * 应退押金
     */
    private int depositCost;

    /**
     * 应付租车押金
     */
    private int depositCostYingfu;
    /**
     * 实付租车押金
     */
    private int depositCostShifu;

    /**
     * 实退违章押金
     */
    private int depositWzCostReal;
    /**
     * 应退违章押金
     */
    private int depositWzCost;

    /**
     * 应付违章押金
     */
    private int depositWzCostYingFu;
    /**
     * 实付违章押金
     */
    private int depositWzCostShifu;
}
