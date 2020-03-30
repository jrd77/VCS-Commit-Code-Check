package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算超运能溢价金额
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:14
 */
@Service
public class OrderOverTransportCapacityPremiumFilter implements OrderCostFilter {


    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
