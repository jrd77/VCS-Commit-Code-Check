package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 租客端配送订单表
 *
 * @author 胡春林
 * @date 2019-12-28 15:55:26
 */
@Mapper
public interface RenterOrderDeliveryMapper {

    RenterOrderDeliveryEntity selectByPrimaryKey(Integer id);

    int insert(RenterOrderDeliveryEntity record);

    int insertSelective(RenterOrderDeliveryEntity record);

    int updateByPrimaryKey(RenterOrderDeliveryEntity record);

    int updateByPrimaryKeySelective(RenterOrderDeliveryEntity record);

    List<RenterOrderDeliveryEntity> listRenterOrderDeliveryByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);

    int updateById(@Param("id") Integer id);

    RenterOrderDeliveryEntity findRenterOrderByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo,@Param("type") Integer type);

}
