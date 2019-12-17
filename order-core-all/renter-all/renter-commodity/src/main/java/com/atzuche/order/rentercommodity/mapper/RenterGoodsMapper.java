package com.atzuche.order.rentercommodity.mapper;

import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租客商品概览表
 * 
 * @author ZhangBin
 * @date 2019-12-17 11:43:16
 */
@Mapper
public interface RenterGoodsMapper{

    RenterGoodsEntity selectByPrimaryKey(Integer id);

    int insert(RenterGoodsEntity record);
    
    int insertSelective(RenterGoodsEntity record);

    int updateByPrimaryKey(RenterGoodsEntity record);
    
    int updateByPrimaryKeySelective(RenterGoodsEntity record);

}
