package com.atzuche.order.ownercost.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;

import java.util.List;

/**
 * 车主增值订单明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 */
@Mapper
public interface OwnerOrderIncrementDetailMapper{

    OwnerOrderIncrementDetailEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderIncrementDetailEntity> selectALL();

    int insert(OwnerOrderIncrementDetailEntity record);
    
    int saveOwnerOrderIncrementDetail(OwnerOrderIncrementDetailEntity record);

    int updateByPrimaryKey(OwnerOrderIncrementDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderIncrementDetailEntity record);

    List<OwnerOrderIncrementDetailEntity> listOwnerOrderIncrementDetail(@Param("orderNo") String orderNo, @Param("ownerOrderNo") String ownerOrderNo);

    Integer saveOwnerOrderIncrementDetailBatch(@Param("costList") List<OwnerOrderIncrementDetailEntity> costList);

}