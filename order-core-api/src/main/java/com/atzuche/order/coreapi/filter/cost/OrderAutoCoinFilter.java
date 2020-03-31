package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算订单凹凸币抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:59
 */
@Service
public class OrderAutoCoinFilter implements OrderCostFilter{

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
