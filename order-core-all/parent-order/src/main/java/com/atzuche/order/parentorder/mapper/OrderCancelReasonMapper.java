package com.atzuche.order.parentorder.mapper;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单取消原因
 * 
 * @author ZhangBin
 * @date 2020-01-08 16:13:04
 */
@Mapper
public interface OrderCancelReasonMapper{

    OrderCancelReasonEntity selectByPrimaryKey(Integer id);

    int insert(OrderCancelReasonEntity record);
    
    int insertSelective(OrderCancelReasonEntity record);

    int updateByPrimaryKey(OrderCancelReasonEntity record);
    
    int updateByPrimaryKeySelective(OrderCancelReasonEntity record);

    OrderCancelReasonEntity selectByOrderNo(@Param("orderNo")String orderNo,@Param("renterOrderNo")String renterOrderNo,
                                            @Param("ownerOrderNo")String ownerOrderNo);


    /**
     * 查询订单取消信息
     * @param orderNo 订单号
     * @return List<OrderCancelReasonEntity>
     */
    List<OrderCancelReasonEntity> selectListByOrderNo(@Param("orderNo")String orderNo);

    List<OrderCancelReasonEntity> selectListByOrderNos(@Param("ownerOrderNos") List<String> ownerOrderNos);
}
