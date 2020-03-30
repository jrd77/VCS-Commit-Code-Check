package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算附加驾驶人保险费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:07
 */
@Service
public class OrderExtraDriverInsureAmtFilter implements OrderCostFilter {

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
