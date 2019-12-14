package com.atzuche.parentorder.mapper;

import com.atzuche.parentorder.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:52:41
 */
@Mapper
public interface OrderMapper{

    OrderEntity selectByPrimaryKey(Integer id);

    List<OrderEntity> selectALL();

    int insert(OrderEntity record);
    
    int insertSelective(OrderEntity record);

    int updateByPrimaryKey(OrderEntity record);
    
    int updateByPrimaryKeySelective(OrderEntity record);

}
