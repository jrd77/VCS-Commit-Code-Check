package com.atzuche.order.coreapi.submitOrder.filter.cost;

import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.filter.cost.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 提交订单相关费用计算
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 9:44 上午
 **/
@Service
public class OrderCostFilterChain implements OrderCostFilter, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 订单费用列表
     */
    private List<OrderCostFilter> orderCostFilterList = new ArrayList<>();


    @PostConstruct
    private void init() {
        //租金
        orderCostFilterList.add(applicationContext.getBean(OrderRentAmtFilter.class));
        //基础保险费
        orderCostFilterList.add(applicationContext.getBean(OrderInsurAmtFilter.class));
        //全面保障费
        orderCostFilterList.add(applicationContext.getBean(OrderAbatementAmtFilter.class));
        //附加驾驶人保险费
        orderCostFilterList.add(applicationContext.getBean(OrderExtraDriverInsureAmtFilter.class));
        //手续费
        orderCostFilterList.add(applicationContext.getBean(OrderServiceChargeFilter.class));
        //取还车服务费用
        orderCostFilterList.add(applicationContext.getBean(OrderGetAndReturnCarCostFilter.class));
        //超运能溢价
        orderCostFilterList.add(applicationContext.getBean(OrderOverTransportCapacityPremiumFilter.class));
    }

    @Override
    public void calculate(OrderCostContext context) throws OrderFilterException {

        if (CollectionUtils.isEmpty(orderCostFilterList)) {
            throw new RuntimeException("Order cost filter list is empty.");
        }
        for (OrderCostFilter filter : orderCostFilterList) {
            filter.calculate(context);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
