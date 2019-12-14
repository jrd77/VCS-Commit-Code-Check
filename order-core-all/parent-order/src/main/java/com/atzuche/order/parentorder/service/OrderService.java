package com.atzuche.order.parentorder.service;

import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 主订单表
 *
 * @author ZhangBin
 * @date 2019-12-12 14:41:07
 */
@Service
public class OrderService{
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取主订单详情
     * @param orderNo 主订单号
     * @return OrderEntity
     */
    public OrderEntity getParentOrderDetailByOrderNo(Long orderNo) {
    	return orderMapper.getParentOrderDetailByOrderNo(orderNo);
    }
}
