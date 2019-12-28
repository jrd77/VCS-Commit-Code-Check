package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.mapper.OrderCouponMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;


/**
 * 订单券表
 *
 * @author ZhangBin
 * @date 2019-12-25 15:32:31
 */
@Service
public class OrderCouponService{

    @Resource
    private OrderCouponMapper orderCouponMapper;


    /**
     * 获取租客子单使用的优惠券
     * @param renterOrderNo
     * @return List<OrderCouponEntity>
     */
    public List<OrderCouponEntity> listOrderCouponByRenterOrderNo(String renterOrderNo) {
    	return orderCouponMapper.listOrderCouponByRenterOrderNo(renterOrderNo);
    }

}
