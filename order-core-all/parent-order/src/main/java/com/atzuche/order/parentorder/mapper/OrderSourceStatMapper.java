package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单来源统计
 * 
 * @author ZhangBin
 * @date 2019-12-24 16:19:32
 */
@Mapper
public interface OrderSourceStatMapper{

    OrderSourceStatEntity selectByOrderNo(String orderNo);

    int insert(OrderSourceStatEntity record);
    
    int insertSelective(OrderSourceStatEntity record);

    int updateByPrimaryKey(OrderSourceStatEntity record);
    
    int updateByPrimaryKeySelective(OrderSourceStatEntity record);

}
