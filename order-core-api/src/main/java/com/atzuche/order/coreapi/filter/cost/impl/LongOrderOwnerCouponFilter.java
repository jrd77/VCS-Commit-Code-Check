package com.atzuche.order.coreapi.filter.cost.impl;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算长租订单车主券抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:56
 */
@Service
public class LongOrderOwnerCouponFilter implements OrderCostFilter {


    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
