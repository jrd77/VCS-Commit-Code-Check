package com.atzuche.order.commons.filter.cost;

/**
 *
 */
public interface OrderCostFilter {

    /**
     * 费用计算
     *
     * @throws OrderCostFilterException 异常信息
     */
    void calculate() throws OrderCostFilterException;
}
