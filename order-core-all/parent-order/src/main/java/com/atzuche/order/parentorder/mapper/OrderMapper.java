package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2019-12-12 14:41:07
 */
@Mapper
public interface OrderMapper{

    OrderEntity selectByPrimaryKey(Integer id);

    List<OrderEntity> selectALL();

    int insert(OrderEntity record);
    
    int insertSelective(OrderEntity record);

    int updateByPrimaryKey(OrderEntity record);
    
    int updateByPrimaryKeySelective(OrderEntity record);
    
    OrderEntity getParentOrderDetailByOrderNo(@Param("orderNo") String orderNo);

}
