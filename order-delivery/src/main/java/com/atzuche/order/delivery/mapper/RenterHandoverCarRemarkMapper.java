package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租客端交车备注表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface RenterHandoverCarRemarkMapper{

    RenterHandoverCarRemarkEntity selectByPrimaryKey(Integer id);

    int insert(RenterHandoverCarRemarkEntity record);
    
    int insertSelective(RenterHandoverCarRemarkEntity record);

    int updateByPrimaryKey(RenterHandoverCarRemarkEntity record);
    
    int updateByPrimaryKeySelective(RenterHandoverCarRemarkEntity record);

}
