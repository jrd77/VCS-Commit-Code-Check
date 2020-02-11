package com.atzuche.order.detain.mapper;

import com.atzuche.order.detain.entity.RenterEventDetainStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 租客端暂扣处理状态表
 * 
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 */
@Mapper
public interface RenterEventDetainStatusMapper{

    RenterEventDetainStatusEntity selectByPrimaryKey(Integer id);

    int insertSelective(RenterEventDetainStatusEntity record);

    int updateByPrimaryKeySelective(RenterEventDetainStatusEntity record);

    RenterEventDetainStatusEntity selectByRentOrderNo(@Param("renterOrderNo") String renterOrderNo);
}
