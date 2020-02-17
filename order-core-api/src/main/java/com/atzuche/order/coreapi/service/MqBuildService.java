package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
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

    /**
     * 构建事件的基本属性
     * @param orderNo
     * @param memNo
     * @return
     */
    public OrderBaseDataMq buildOrderBaseDataMq(String orderNo,String memNo){
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);


        OrderBaseDataMq orderBaseDataMq = new OrderBaseDataMq();
        orderBaseDataMq.setCategory(orderEntity.getCategory().toString());
        orderBaseDataMq.setMemNo(null != memNo ? Integer.parseInt(memNo) : null);
        orderBaseDataMq.setBusinessChildType(osse.getBusinessChildType());
        orderBaseDataMq.setBusinessParentType(osse.getBusinessParentType());
        orderBaseDataMq.setOrderNo(orderNo);
        orderBaseDataMq.setPlatformChildType(osse.getPlatformChildType());
        orderBaseDataMq.setPlatformParentType(osse.getPlatformParentType());
        orderBaseDataMq.setCarNo(Integer.parseInt(renterOrderEntity.getGoodsCode()));
        orderBaseDataMq.setRentTime(LocalDateTimeUtils.localDateTimeToDate(renterOrderEntity.getActRentTime()));
        orderBaseDataMq.setRevertTime(LocalDateTimeUtils.localDateTimeToDate(renterOrderEntity.getActRevertTime()));

        return orderBaseDataMq;
    }
}
