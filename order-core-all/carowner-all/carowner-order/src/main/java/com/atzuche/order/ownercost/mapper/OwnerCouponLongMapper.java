package com.atzuche.order.ownercost.mapper;

import com.atzuche.order.ownercost.entity.OwnerCouponLongEntity;
import org.apache.ibatis.annotations.Mapper;

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

}
