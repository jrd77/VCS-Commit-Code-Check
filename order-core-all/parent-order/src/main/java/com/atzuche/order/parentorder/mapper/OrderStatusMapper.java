package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主订单表状态
 * 
 * @author ZhangBin
 * @date 2019-12-12 14:41:07
 */
@Mapper
public interface OrderStatusMapper{

    OrderStatusEntity selectByPrimaryKey(Integer id);

    List<OrderStatusEntity> selectALL();

    int insert(OrderStatusEntity record);
    
    int insertSelective(OrderStatusEntity record);

    int updateByPrimaryKey(OrderStatusEntity record);
    
    int updateByPrimaryKeySelective(OrderStatusEntity record);
    
    OrderStatusEntity getOrderStatusByOrderNo(@Param("orderNo") Long orderNo);

}
