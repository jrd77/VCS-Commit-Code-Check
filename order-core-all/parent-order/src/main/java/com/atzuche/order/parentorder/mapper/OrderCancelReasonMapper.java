package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单取消原因
 * 
 * @author ZhangBin
 * @date 2020-01-08 16:13:04
 */
@Mapper
public interface OrderCancelReasonMapper{

    OrderCancelReasonEntity selectByPrimaryKey(Integer id);

    int insert(OrderCancelReasonEntity record);
    
    int insertSelective(OrderCancelReasonEntity record);

    int updateByPrimaryKey(OrderCancelReasonEntity record);
    
    int updateByPrimaryKeySelective(OrderCancelReasonEntity record);

}
