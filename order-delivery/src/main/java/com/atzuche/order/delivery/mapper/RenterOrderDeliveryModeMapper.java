package com.atzuche.order.delivery.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;

@Mapper
public interface RenterOrderDeliveryModeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RenterOrderDeliveryMode record);

    int insertSelective(RenterOrderDeliveryMode record);

    RenterOrderDeliveryMode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RenterOrderDeliveryMode record);

    int updateByPrimaryKey(RenterOrderDeliveryMode record);
    
    RenterOrderDeliveryMode getDeliveryModeByRenterOrderNo(String renterOrderNo);
}