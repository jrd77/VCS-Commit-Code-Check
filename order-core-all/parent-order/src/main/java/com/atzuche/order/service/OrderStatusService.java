package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.OrderStatusMapper;
import com.atzuche.order.entity.OrderStatusEntity;



/**
 * 主订单表状态
 *
 * @author ZhangBin
 * @date 2019-12-12 14:41:07
 */
@Service
public class OrderStatusService{
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    /**
     * 获取订单状态总表
     * @param orderNo 主订单号
     * @return OrderStatusEntity
     */
    public OrderStatusEntity getOrderStatusByOrderNo(Long orderNo) {
    	return orderStatusMapper.getOrderStatusByOrderNo(orderNo);
    }
}
