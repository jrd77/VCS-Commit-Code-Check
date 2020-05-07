package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 主订单表
 * 
 * @author ZhangBin
 * @date 2020-04-07 11:18:55
 */
@Mapper
public interface OwnerCouponLongMapper{

    OwnerCouponLongEntity selectByPrimaryKey(Integer id);

    int insert(OwnerCouponLongEntity record);
    
    int insertSelective(OwnerCouponLongEntity record);

    int updateByPrimaryKey(OwnerCouponLongEntity record);
    
    int updateByPrimaryKeySelective(OwnerCouponLongEntity record);

    OwnerCouponLongEntity getByRenterOrderNo(@Param("renterOrderNo")String renterOrderNo);
}
