package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 下单完成记录表
 * 
 * @author ZhangBin
 * @date 2020-01-01 15:06:16
 */
@Mapper
public interface OrderRecordMapper{

    OrderRecordEntity selectByPrimaryKey(Integer id);

    int insert(OrderRecordEntity record);
    
    int insertSelective(OrderRecordEntity record);

    int updateByPrimaryKey(OrderRecordEntity record);
    
    int updateByPrimaryKeySelective(OrderRecordEntity record);

}
