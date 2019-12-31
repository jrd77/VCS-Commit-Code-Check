package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.DeliveryHttpLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 胡春林
 * @date 2019-12-27 16:25:53
 */
@Mapper
public interface DeliveryHttpLogMapper{

    DeliveryHttpLogEntity selectByPrimaryKey(Integer id);

    int insert(DeliveryHttpLogEntity record);
    
    int insertSelective(DeliveryHttpLogEntity record);

    int updateByPrimaryKey(DeliveryHttpLogEntity record);
    
    int updateByPrimaryKeySelective(DeliveryHttpLogEntity record);

}
