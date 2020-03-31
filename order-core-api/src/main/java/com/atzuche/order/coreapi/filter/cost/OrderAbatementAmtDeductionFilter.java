package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import org.springframework.stereotype.Service;

/**
 * 计算订单全面保障服务减免金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:13
 */
@Service
public class OrderAbatementAmtDeductionFilter implements OrderCostFilter {
    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

    }
}
