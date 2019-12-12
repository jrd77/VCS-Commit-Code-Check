package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.OrderMapper;
import com.atzuche.order.entity.OrderEntity;



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


}
