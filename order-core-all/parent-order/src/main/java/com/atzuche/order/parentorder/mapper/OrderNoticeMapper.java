package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderNoticeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author ZhangBin
 * @date 2020-03-11 15:34:01
 */
@Mapper
public interface OrderNoticeMapper {

    OrderNoticeEntity selectByPrimaryKey(Integer id);

    int insert(OrderNoticeEntity record);
    
    int insertSelective(OrderNoticeEntity record);

    int updateByPrimaryKey(OrderNoticeEntity record);
    
    int updateByPrimaryKeySelective(OrderNoticeEntity record);

    List<OrderNoticeEntity> queryByOrderNo(@Param("orderNo") String orderNo);
}
