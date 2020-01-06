package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主端交车备注表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface OwnerHandoverCarRemarkMapper{

    OwnerHandoverCarRemarkEntity selectByPrimaryKey(Integer id);

    int insert(OwnerHandoverCarRemarkEntity record);
    
    int insertSelective(OwnerHandoverCarRemarkEntity record);

    int updateByPrimaryKey(OwnerHandoverCarRemarkEntity record);
    
    int updateByPrimaryKeySelective(OwnerHandoverCarRemarkEntity record);

    /**
     * 根据订单号和类型查询
     * @param orderNo
     * @return
     */
    List<OwnerHandoverCarRemarkEntity> selectObjectByOrderNo(@Param("orderNo") String orderNo);

}
