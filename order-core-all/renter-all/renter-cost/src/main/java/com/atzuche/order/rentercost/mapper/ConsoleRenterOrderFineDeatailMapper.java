package com.atzuche.order.rentercost.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;

/**
 * 全局的租客订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Mapper
public interface ConsoleRenterOrderFineDeatailMapper{

    ConsoleRenterOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    int insert(ConsoleRenterOrderFineDeatailEntity record);
    
    int insertSelective(ConsoleRenterOrderFineDeatailEntity record);

    int updateByPrimaryKey(ConsoleRenterOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(ConsoleRenterOrderFineDeatailEntity record);

    List<ConsoleRenterOrderFineDeatailEntity> listConsoleRenterOrderFineDeatail(@Param("orderNo") String orderNo,@Param("memNo") String memNo);
}
