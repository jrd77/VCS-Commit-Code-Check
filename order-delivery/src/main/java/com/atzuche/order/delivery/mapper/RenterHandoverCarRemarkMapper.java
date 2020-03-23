package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 根据订单号查询
     * @param orderNo
     * @return
     */
    List<RenterHandoverCarRemarkEntity> selectObjectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单号和类型查询
     * @param orderNo
     * @param type
     * @return
     */
    RenterHandoverCarRemarkEntity selectObjectByOrderNoType(@Param("orderNo") String orderNo,@Param("type") Integer type);


    /**
     * 根据子订单号和类型获取
     * @param renterOrderNo
     * @param type
     * @return
     */
    RenterHandoverCarRemarkEntity findRemarkObjectByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo, @Param("type") Integer type);

    /**
     * 获取流程数据
     * @param orderNo
     * @return
     */
    List<RenterHandoverCarRemarkEntity> selectProIdByOrderNo(@Param("orderNo") String orderNo);
}
