package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.OrderCouponEntity;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单券表
 * 
 * @author ZhangBin
 * @date 2019-12-25 15:32:31
 */
@Mapper
public interface OrderCouponMapper{

    OrderCouponEntity selectByPrimaryKey(Integer id);

    OrderCouponEntity selectByOrderNo(String orderNo);

    int insert(OrderCouponEntity record);
    
    int insertSelective(OrderCouponEntity record);

    int updateByPrimaryKey(OrderCouponEntity record);
    
    int updateByPrimaryKeySelective(OrderCouponEntity record);
    
    List<OrderCouponEntity> listOrderCouponByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);

}
