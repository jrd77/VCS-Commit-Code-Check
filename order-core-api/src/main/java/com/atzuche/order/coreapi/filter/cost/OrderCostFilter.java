package com.atzuche.order.coreapi.filter.cost;


import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;

/**
 * 费用计算
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 9:44 上午
 */
public interface OrderCostFilter {

    /**
     * 费用计算
     *
     * @param context 参数及结果
     * @throws OrderCostFilterException 异常信息
     */
    void calculate(OrderCostContext context) throws OrderCostFilterException;
}
