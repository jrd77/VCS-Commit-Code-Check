package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.OrderDeliveryFlowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发送仁云信息表
 * @author 胡春林
 */
@Mapper
public interface OrderDeliveryFlowMapper{

    OrderDeliveryFlowEntity selectByPrimaryKey(Integer id);

    int insert(OrderDeliveryFlowEntity record);
    
    int insertSelective(OrderDeliveryFlowEntity record);

    int updateByPrimaryKey(OrderDeliveryFlowEntity record);
    
    int updateByPrimaryKeySelective(OrderDeliveryFlowEntity record);

    OrderDeliveryFlowEntity selectOrderDeliveryByOrderNo(@Param("orderNo") String orderNo,@Param("serviceType") String serviceType);

    List<OrderDeliveryFlowEntity> selectOrderDeliveryByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);

}
