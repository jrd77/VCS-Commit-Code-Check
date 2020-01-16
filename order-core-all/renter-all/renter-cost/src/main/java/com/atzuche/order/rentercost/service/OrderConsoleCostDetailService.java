package com.atzuche.order.rentercost.service;

import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleCostDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 后台管理操作费用表（无条件补贴）
 *
 * @author ZhangBin
 * @date 2020-01-16 15:03:47
 */
@Service
public class OrderConsoleCostDetailService{
    @Autowired
    private OrderConsoleCostDetailMapper orderConsoleCostDetailMapper;

    public List<OrderConsoleCostDetailEntity> getOrderConsoleCostDetaiByOrderNo(String orderNo){
            return orderConsoleCostDetailMapper.selectByOrderNo(orderNo);
    }
}
