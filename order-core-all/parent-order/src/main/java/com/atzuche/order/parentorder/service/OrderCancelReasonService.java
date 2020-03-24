package com.atzuche.order.parentorder.service;

import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.mapper.OrderCancelReasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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

    public int updateOrderCancelReasonRecord(OrderCancelReasonEntity entity){
        return orderCancelReasonMapper.updateByPrimaryKeySelective(entity);
    }


    public OrderCancelReasonEntity selectByOrderNo(String orderNo,String renterOrderNo,String ownerOrderNo){
        return orderCancelReasonMapper.selectByOrderNo(orderNo, renterOrderNo, ownerOrderNo);
    }


    public List<OrderCancelReasonEntity> selectListByOrderNo(String orderNo) {
        return orderCancelReasonMapper.selectListByOrderNo(orderNo);
    }

    public List<OrderCancelReasonEntity> selectListByOrderNos(List<String> ownerOrderNos) {
        return orderCancelReasonMapper.selectListByOrderNos(ownerOrderNos);
    }
}
