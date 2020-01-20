package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 主订单表状态
 * 
 * @author ZhangBin
 * @date 2019-12-24 16:19:32
 */
@Mapper
public interface OrderStatusMapper{

    OrderStatusEntity selectByOrderNo(String orderNo);

    int insert(OrderStatusEntity record);
    
    int insertSelective(OrderStatusEntity record);

    int updateByPrimaryKey(OrderStatusEntity record);
    
    int updateByPrimaryKeySelective(OrderStatusEntity record);

    List<String> queryOrderNoByStartTimeAndEndTime(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    int updateRenterOrderByOrderNo(OrderStatusEntity orderStatusEntity);

    Integer getStatusByOrderNo(@Param("orderNo") String orderNo);
    
    Integer updateDispatchStatus(@Param("orderNo") String orderNo, @Param("dispatchStatus") Integer dispatchStatus);
}
