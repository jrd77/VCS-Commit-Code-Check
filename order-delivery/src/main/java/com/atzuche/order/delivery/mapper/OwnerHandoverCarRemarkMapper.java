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

    /**
     * 根据子订单号
     * @param ownerOrderNo
     * @return
     */
    List<OwnerHandoverCarRemarkEntity> selectOwnerRemarkByOrderNo(@Param("ownerOrderNo") String ownerOrderNo);

    /**
     * 根据订单号和类型查询
     * @param orderNo
     * @param type
     * @return
     */
    OwnerHandoverCarRemarkEntity selectObjectByOrderNoType(@Param("orderNo") String orderNo,@Param("type") Integer type);

    /**
     * 根据子订单号和类型获取
     * @param ownerOrderNo
     * @param type
     * @return
     */
    OwnerHandoverCarRemarkEntity findRemarkObjectByRenterOrderNo(@Param("ownerOrderNo") String ownerOrderNo,@Param("type") Integer type);

    /**
     * 获取流程数据
     * @param orderNo
     * @return
     */
    List<OwnerHandoverCarRemarkEntity> selectProIdByOrderNo(@Param("orderNo") String orderNo);
}
