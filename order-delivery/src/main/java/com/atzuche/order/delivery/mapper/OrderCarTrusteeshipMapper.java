package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 托管车信息表
 * @author 胡春林
 */
@Mapper
public interface OrderCarTrusteeshipMapper{

    OrderCarTrusteeshipEntity selectByPrimaryKey(Integer id);

    int insert(OrderCarTrusteeshipEntity record);
    
    int insertSelective(OrderCarTrusteeshipEntity record);

    int updateByPrimaryKey(OrderCarTrusteeshipEntity record);
    
    int updateByPrimaryKeySelective(OrderCarTrusteeshipEntity record);

}
