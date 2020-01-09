package com.atzuche.order.ownercost.mapper;

import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车主订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2020-01-09 11:30:07
 */
@Mapper
public interface OwnerOrderFineDeatailMapper{

    OwnerOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    int insert(OwnerOrderFineDeatailEntity record);
    
    int insertSelective(OwnerOrderFineDeatailEntity record);

    int updateByPrimaryKey(OwnerOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderFineDeatailEntity record);

}
