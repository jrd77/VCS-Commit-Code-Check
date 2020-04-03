package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租客配送地址表
 *
 * @author 胡春林
 * @date 2019-12-28 15:57:03
 */
@Mapper
public interface RenterDeliveryAddrMapper{

    RenterDeliveryAddrEntity selectByPrimaryKey(Integer id);

    int insert(RenterDeliveryAddrEntity record);
    
    int insertSelective(RenterDeliveryAddrEntity record);

    int updateByPrimaryKey(RenterDeliveryAddrEntity record);
    
    int updateByPrimaryKeySelective(RenterDeliveryAddrEntity record);

    RenterDeliveryAddrEntity selectByRenterOrderNo(String renterOrderNo);

    RenterDeliveryAddrEntity selectByOrderNo(String orderNo);

}
