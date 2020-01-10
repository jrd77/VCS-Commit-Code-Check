package com.atzuche.order.rentercost.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;

/**
 * 订单补付表
 * 
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Mapper
public interface OrderSupplementDetailMapper{

    OrderSupplementDetailEntity selectByPrimaryKey(Integer id);

    int insert(OrderSupplementDetailEntity record);
    
    int insertSelective(OrderSupplementDetailEntity record);

    int updateByPrimaryKey(OrderSupplementDetailEntity record);
    
    int updateByPrimaryKeySelective(OrderSupplementDetailEntity record);
    
    List<OrderSupplementDetailEntity> listOrderSupplementDetailByOrderNoAndMemNo(@Param("orderNo") String orderNo,@Param("memNo") String memNo);
    
    Integer updatePayFlagById(@Param("id") Integer id, @Param("payFlag") Integer payFlag);

}
