package com.atzuche.order.owner.cost.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.owner.cost.entity.ConsoleOwnerOrderFineDeatailEntity;


/**
 * 全局的车主订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Mapper
public interface ConsoleOwnerOrderFineDeatailMapper{

    ConsoleOwnerOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    int insert(ConsoleOwnerOrderFineDeatailEntity record);
    
    int insertSelective(ConsoleOwnerOrderFineDeatailEntity record);

    int updateByPrimaryKey(ConsoleOwnerOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(ConsoleOwnerOrderFineDeatailEntity record);

}
