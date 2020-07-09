package com.atzuche.order.coreapi.submit.filter.cost;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.filter.cost.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 提交长租订单相关费用计算
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 9:44 上午
 **/
@Service
@Slf4j
public class LongSubmitOrderBeforeCostFilterChain implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 订单费用列表
     */
    private List<OrderCostFilter> orderCostFilterList = new ArrayList<>();


    @PostConstruct
    private void init() {
        //租金
        orderCostFilterList.add(applicationContext.getBean(OrderRentAmtFilter.class));
        //租金折扣
        orderCostFilterList.add(applicationContext.getBean(LongOrderOwnerCouponFilter.class));
        //附加驾驶人保险费
        orderCostFilterList.add(applicationContext.getBean(OrderExtraDriverInsureAmtFilter.class));
        //手续费
        orderCostFilterList.add(applicationContext.getBean(OrderServiceChargeFilter.class));
        //取还车服务费用
        orderCostFilterList.add(applicationContext.getBean(OrderGetAndReturnCarCostFilter.class));
        //超运能溢价
        orderCostFilterList.add(applicationContext.getBean(OrderOverTransportCapacityPremiumFilter.class));
        // 取还车精准达服务费
        orderCostFilterList.add(applicationContext.getBean(AccurateSrvCostFilter.class));
        //取还车、超运能溢价费用补贴
        orderCostFilterList.add(applicationContext.getBean(LongOrderGetAndReturnCarCostSubsidyFilter.class));
        //平台优惠券抵扣
        //orderCostFilterList.add(applicationContext.getBean(OrderPlatformCouponFilter.class));
        //车辆押金
        orderCostFilterList.add(applicationContext.getBean(OrderCarDepositAmtFilter.class));
        //违章押金
        orderCostFilterList.add(applicationContext.getBean(OrderIllegalDepositAmtFilter.class));
    }

    public void calculate(OrderCostContext context) throws OrderFilterException {
        log.info("长租订单相关费用计算开始. context:[{}]", JSON.toJSONString(context));
        if (CollectionUtils.isEmpty(orderCostFilterList)) {
            throw new RuntimeException("Long lease order cost filter list is empty.");
        }
        for (OrderCostFilter filter : orderCostFilterList) {
            filter.calculate(context);
        }
        log.info("长租订单相关费用计算结束. context:[{}]", JSON.toJSONString(context));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
