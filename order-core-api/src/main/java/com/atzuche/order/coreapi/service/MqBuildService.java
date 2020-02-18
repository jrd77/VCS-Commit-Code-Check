package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.event.rabbit.neworder.OrderBaseDataMq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/14 5:02 下午
 **/
@Service
public class MqBuildService {
    @Autowired
    private OrderSourceStatService orderSourceStatService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OwnerOrderService ownerOrderService;

    /**
     * 构建事件的基本属性
     * @param orderNo
     * @return
     */
    public OrderBaseDataMq buildOrderBaseDataMq(String orderNo){
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);

        OrderBaseDataMq orderBaseDataMq = new OrderBaseDataMq();
        orderBaseDataMq.setCategory(orderEntity.getCategory().toString());
        orderBaseDataMq.setRenterMemNo(null != renterOrderEntity && null != renterOrderEntity.getRenterMemNo() ? Integer.parseInt(renterOrderEntity.getRenterMemNo()) : null);
        orderBaseDataMq.setOwnerMemNo(null != ownerOrderEntity && null != ownerOrderEntity.getMemNo() ? Integer.valueOf(ownerOrderEntity.getMemNo()) : null);
        orderBaseDataMq.setBusinessChildType(osse.getBusinessChildType());
        orderBaseDataMq.setBusinessParentType(osse.getBusinessParentType());
        orderBaseDataMq.setOrderNo(orderNo);
        orderBaseDataMq.setPlatformChildType(osse.getPlatformChildType());
        orderBaseDataMq.setPlatformParentType(osse.getPlatformParentType());
        orderBaseDataMq.setCarNo(Integer.parseInt(renterOrderEntity.getGoodsCode()));

        orderBaseDataMq.setRentTime(null != renterOrderEntity.getActRentTime() ? LocalDateTimeUtils.localDateTimeToDate(renterOrderEntity.getActRentTime()) : LocalDateTimeUtils.localDateTimeToDate(renterOrderEntity.getExpRentTime()));
        orderBaseDataMq.setRevertTime(null != renterOrderEntity.getActRevertTime() ? LocalDateTimeUtils.localDateTimeToDate(renterOrderEntity.getActRevertTime()) : LocalDateTimeUtils.localDateTimeToDate(renterOrderEntity.getExpRevertTime()));

        return orderBaseDataMq;
    }
}
