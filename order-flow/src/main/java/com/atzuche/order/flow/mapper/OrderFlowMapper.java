package com.atzuche.order.flow.mapper;

import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.atzuche.order.flow.entity.OrderFlowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客端交易流程表
 * 
 * @author ZhangBin
 * @date 2020-01-01 15:10:51
 */
@Mapper
public interface OrderFlowMapper{

    OrderFlowEntity selectByPrimaryKey(Integer id);

    int insert(OrderFlowEntity record);
    
    int insertSelective(OrderFlowEntity record);

    int updateByPrimaryKey(OrderFlowEntity record);
    
    int updateByPrimaryKeySelective(OrderFlowEntity record);

    List<OrderFlowEntity> selectOrderFlowListByOrderNo(OrderFlowRequestDTO orderFlowRequestVO);

    OrderFlowEntity getByOrderNoAndStatus(@Param("oderNo")String oderNo, @Param("orderStatus")Integer orderStatus);
}
