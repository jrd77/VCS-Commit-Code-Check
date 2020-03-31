package com.atzuche.order.coreapi.filter.cost.impl;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算订单取送车服务券抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:57
 */
@Service
public class OrderGetCarFeeCouponFilter implements OrderCostFilter{


    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
