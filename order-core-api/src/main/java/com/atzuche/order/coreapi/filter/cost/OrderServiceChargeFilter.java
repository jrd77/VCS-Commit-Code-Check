package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算手续费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:09
 */
@Service
public class OrderServiceChargeFilter implements OrderCostFilter {

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
