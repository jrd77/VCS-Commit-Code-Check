package com.atzuche.order.rentercost.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;

/**
 * 后台管理操补贴明细表（无条件补贴）
 * 
 * @author ZhangBin
 * @date 2019-12-18 14:37:56
 */
@Mapper
public interface OrderConsoleCostDetailMapper{

    OrderConsoleCostDetailEntity selectByPrimaryKey(Integer id);

    int insert(OrderConsoleCostDetailEntity record);
    
    int saveOrderConsoleCostDetail(OrderConsoleCostDetailEntity record);

    int updateByPrimaryKey(OrderConsoleCostDetailEntity record);
    
    int updateByPrimaryKeySelective(OrderConsoleCostDetailEntity record);
    
    List<OrderConsoleCostDetailEntity> listOrderConsoleCostDetail(@Param("orderNo") String orderNo, @Param("memNo") String memNo);

}
