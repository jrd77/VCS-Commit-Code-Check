package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import org.apache.ibatis.annotations.Mapper;

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

}
