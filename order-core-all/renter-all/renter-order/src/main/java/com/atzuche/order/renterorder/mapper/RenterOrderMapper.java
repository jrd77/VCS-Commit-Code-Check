package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Mapper
public interface RenterOrderMapper{

    RenterOrderEntity selectByPrimaryKey(Integer id);

    List<RenterOrderEntity> selectALL();

    int insert(RenterOrderEntity record);
    
    int insertSelective(RenterOrderEntity record);

    int updateByPrimaryKey(RenterOrderEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderEntity record);

    List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(@Param("orderNo") String orderNo);

    RenterOrderEntity getRenterOrderByOrderNoAndIsEffective(@Param("orderNo") String orderNo);
}
