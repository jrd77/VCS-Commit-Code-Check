package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台管理操作费用表（无条件补贴）
 * 
 * @author ZhangBin
 * @date 2020-01-16 15:03:47
 */
@Mapper
public interface OrderConsoleCostDetailMapper{

    OrderConsoleCostDetailEntity selectByPrimaryKey(Integer id);

    int insert(OrderConsoleCostDetailEntity record);
    
    int insertSelective(OrderConsoleCostDetailEntity record);

    int updateByPrimaryKey(OrderConsoleCostDetailEntity record);
    
    int updateByPrimaryKeySelective(OrderConsoleCostDetailEntity record);

//    List<OrderConsoleCostDetailEntity> selectByOrderNo(@Param("orderNo")String orderNo);

    List<OrderConsoleCostDetailEntity> selectByOrderNoAndMemNo(@Param("orderNo")String orderNo,@Param("memNo")String memNo);
}
