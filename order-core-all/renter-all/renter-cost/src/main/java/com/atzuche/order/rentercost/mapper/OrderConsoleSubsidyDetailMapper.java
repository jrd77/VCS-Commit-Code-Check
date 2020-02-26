package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 后台管理操补贴明细表（无条件补贴）
 * 
 * @author ZhangBin
 * @date 2020-01-01 11:01:58
 */
@Mapper
public interface OrderConsoleSubsidyDetailMapper{

    OrderConsoleSubsidyDetailEntity selectByPrimaryKey(Integer id);

    int insert(OrderConsoleSubsidyDetailEntity record);
    
    int insertSelective(OrderConsoleSubsidyDetailEntity record);

    int updateByPrimaryKey(OrderConsoleSubsidyDetailEntity record);
    
    int updateByPrimaryKeySelective(OrderConsoleSubsidyDetailEntity record);
    
    List<OrderConsoleSubsidyDetailEntity> listOrderConsoleSubsidyDetailByOrderNoAndMemNo(@Param("orderNo") String orderNo, @Param("memNo") String memNo);

    Integer deleteConsoleSubsidyByOrderNoAndCode(@Param("orderNo") String orderNo, @Param("subsidyCostCode") String subsidyCostCode);
}
