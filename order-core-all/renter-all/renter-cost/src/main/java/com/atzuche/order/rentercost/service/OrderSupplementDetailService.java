package com.atzuche.order.rentercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderSupplementDetailMapper;



/**
 * 订单补付表
 *
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Service
public class OrderSupplementDetailService{
    @Autowired
    private OrderSupplementDetailMapper orderSupplementDetailMapper;


    /**
     * 获取租客补付记录
     * @param orderNo 主订单号
     * @param memNo 会员号
     * @return List<OrderSupplementDetailEntity>
     */
    public List<OrderSupplementDetailEntity> listOrderSupplementDetailByOrderNoAndMemNo(String orderNo, String memNo) {
    	return orderSupplementDetailMapper.listOrderSupplementDetailByOrderNoAndMemNo(orderNo, memNo);
    }
    
    /**
     * 保存租客补付记录
     * @param supplementEntity 补付记录
     * @return Integer
     */
    public Integer saveOrderSupplementDetail(OrderSupplementDetailEntity supplementEntity) {
    	return orderSupplementDetailMapper.insertSelective(supplementEntity);
    }
}
