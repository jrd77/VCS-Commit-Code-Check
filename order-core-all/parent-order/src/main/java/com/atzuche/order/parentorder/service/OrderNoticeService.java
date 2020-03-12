package com.atzuche.order.parentorder.service;


import com.atzuche.order.parentorder.entity.OrderNoticeEntity;
import com.atzuche.order.parentorder.mapper.OrderNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 
 *
 * @author ZhangBin
 * @date 2020-03-11 15:34:01
 */
@Service
public class OrderNoticeService {
    @Autowired
    private OrderNoticeMapper orderNoticeMapper;

    /**
     * 根据订单号查询
     * @return
     */
    public List<OrderNoticeEntity> queryByOrderNo(String orderNo){
        return orderNoticeMapper.queryByOrderNo(orderNo);
    }

    /**
     * 插入数据
     * @param orderNoticeEntity
     * @return
     */
    public int insert(OrderNoticeEntity orderNoticeEntity){
        return orderNoticeMapper.insertSelective(orderNoticeEntity);
    }

}
