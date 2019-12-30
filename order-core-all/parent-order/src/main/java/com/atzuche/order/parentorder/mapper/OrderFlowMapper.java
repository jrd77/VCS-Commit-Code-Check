package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderFlowEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租客端交易流程表
 * 
 * @author ZhangBin
 * @date 2019-12-30 11:16:42
 */
@Mapper
public interface OrderFlowMapper{

    OrderFlowEntity selectByPrimaryKey(Integer id);

    int insert(OrderFlowEntity record);
    
    int insertSelective(OrderFlowEntity record);

    int updateByPrimaryKey(OrderFlowEntity record);
    
    int updateByPrimaryKeySelective(OrderFlowEntity record);

}
