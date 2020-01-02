package com.atzuche.order.flow.service;

import com.atzuche.order.commons.OrderStatus;
import com.atzuche.order.flow.entity.OrderFlowEntity;
import com.atzuche.order.flow.mapper.OrderFlowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 租客端交易流程表(主订单状态变化过程记录)
 *
 * @author ZhangBin
 * @date 2020-01-01 15:10:51
 */
@Service
public class OrderFlowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFlowService.class);

    @Autowired
    private OrderFlowMapper orderFlowMapper;


    public void inserOrderStatusChangeProcessInfo(String orderNo, OrderStatus orderStatus) {
        LOGGER.info("Add order status change records. param is >> orderNo:[{}],orderStatus:[{}]", orderNo, orderStatus);
        OrderFlowEntity record = new OrderFlowEntity();
        record.setOrderNo(orderNo);
        record.setOrderStatus(orderStatus.getStatus());
        record.setOrderStatusDesc(orderStatus.getDesc());

        int reslut = orderFlowMapper.insert(record);
        LOGGER.info("Add order status change records. result is: [{}]", reslut);
    }


}
