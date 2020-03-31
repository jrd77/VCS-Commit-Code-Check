package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算订单基础保险减免金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:12
 */

@Service
public class OrderInsurAmtDeductionFilter implements OrderCostFilter {

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {


    }
}
