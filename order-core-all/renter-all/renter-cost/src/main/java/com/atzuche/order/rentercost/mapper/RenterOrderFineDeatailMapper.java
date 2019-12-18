package com.atzuche.order.rentercost.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;

import java.util.List;

/**
 * 租客订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Mapper
public interface RenterOrderFineDeatailMapper{

    RenterOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    List<RenterOrderFineDeatailEntity> selectALL();

    int insert(RenterOrderFineDeatailEntity record);
    
    int saveRenterOrderFineDeatail(RenterOrderFineDeatailEntity record);

    int updateByPrimaryKey(RenterOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderFineDeatailEntity record);
    
    List<RenterOrderFineDeatailEntity> listRenterOrderFineDeatail(@Param("orderNo") String orderNo, @Param("renterOrderNo") String renterOrderNo);

}
