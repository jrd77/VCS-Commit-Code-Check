package com.atzuche.order.parentorder.service;

import com.atzuche.order.parentorder.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 主订单表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:52:41
 */
@Service
public class OrderService{
    @Autowired
    private OrderMapper orderMapper;


}
