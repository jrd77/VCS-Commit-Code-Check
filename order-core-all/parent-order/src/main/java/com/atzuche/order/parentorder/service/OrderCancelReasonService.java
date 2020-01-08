package com.atzuche.order.parentorder.service;

import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.mapper.OrderCancelReasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 订单取消原因
 *
 * @author ZhangBin
 * @date 2020-01-08 16:13:04
 */
@Service
public class OrderCancelReasonService{

    @Autowired
    private OrderCancelReasonMapper orderCancelReasonMapper;



    public int addOrderCancelReasonRecord(OrderCancelReasonEntity entity) {

        return orderCancelReasonMapper.insertSelective(entity);
    }



}
