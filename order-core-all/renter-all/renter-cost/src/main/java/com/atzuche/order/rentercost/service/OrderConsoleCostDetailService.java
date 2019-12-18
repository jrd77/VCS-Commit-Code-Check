package com.atzuche.order.rentercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleCostDetailMapper;



/**
 * 后台管理操补贴明细表（无条件补贴）
 *
 * @author ZhangBin
 * @date 2019-12-18 14:37:56
 */
@Service
public class OrderConsoleCostDetailService{
    @Autowired
    private OrderConsoleCostDetailMapper orderConsoleCostDetailMapper;


    /**
     * 获取管理后台补贴
     * @param orderNo
     * @return List<OrderConsoleCostDetailEntity>
     */
    public List<OrderConsoleCostDetailEntity> listOrderConsoleCostDetail(String orderNo, String memNo) {
    	return orderConsoleCostDetailMapper.listOrderConsoleCostDetail(orderNo, orderNo);
    }
    
    /**
     * 保存管理后台补贴
     * @param orderConsoleCostDetailEntity
     * @return
     */
    public Integer saveOrderConsoleCostDetail(OrderConsoleCostDetailEntity orderConsoleCostDetailEntity) {
    	return orderConsoleCostDetailMapper.saveOrderConsoleCostDetail(orderConsoleCostDetailEntity);
    }
}
