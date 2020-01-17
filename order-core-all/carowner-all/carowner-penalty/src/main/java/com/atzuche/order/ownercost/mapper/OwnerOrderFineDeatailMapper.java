package com.atzuche.order.ownercost.mapper;

import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2020-01-09 11:30:07
 */
@Mapper
public interface OwnerOrderFineDeatailMapper{

    OwnerOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderFineDeatailEntity> selectByOrderNo(@Param("orderNo") String orderNo);

    int insert(OwnerOrderFineDeatailEntity record);
    
    int insertSelective(OwnerOrderFineDeatailEntity record);

    int updateByPrimaryKey(OwnerOrderFineDeatailEntity record);
    
    int updateByCashNoAndOwnerOrderNo(OwnerOrderFineDeatailEntity record);

    List<OwnerOrderFineDeatailEntity> getOwnerOrderFineDeatailByOwnerOrderNo(@Param("ownerOrderNo")String ownerOrderNo);

    OwnerOrderFineDeatailEntity getByCashNoAndOwnerOrderNo(@Param("ownerOrderNo")String ownerOrderNo, @Param("fineType")Integer fineType);
}
