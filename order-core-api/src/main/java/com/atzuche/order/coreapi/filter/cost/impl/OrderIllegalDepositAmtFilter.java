package com.atzuche.order.coreapi.filter.cost.impl;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算订单违章押金
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:01
 */
@Service
public class OrderIllegalDepositAmtFilter implements OrderCostFilter {

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
