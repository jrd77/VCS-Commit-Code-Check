package com.atzuche.order.parentorder.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.parentorder.entity.OrderStopFreightInfo;

@Mapper
public interface OrderStopFreightInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderStopFreightInfo record);

    int insertSelective(OrderStopFreightInfo record);

    OrderStopFreightInfo getOrderStopFreightInfoByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(OrderStopFreightInfo record);

    int updateByPrimaryKey(OrderStopFreightInfo record);
    
    Integer getCountByOrderNo(String orderNo);
}