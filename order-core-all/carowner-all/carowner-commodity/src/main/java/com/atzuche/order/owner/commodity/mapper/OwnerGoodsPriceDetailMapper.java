package com.atzuche.order.owner.commodity.mapper;

import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主端商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-17 20:30:11
 */
@Mapper
public interface OwnerGoodsPriceDetailMapper{

    OwnerGoodsPriceDetailEntity selectByPrimaryKey(Integer id);

    int insert(OwnerGoodsPriceDetailEntity record);
    
    int insertSelective(OwnerGoodsPriceDetailEntity record);

    int updateByPrimaryKey(OwnerGoodsPriceDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerGoodsPriceDetailEntity record);

    List<OwnerGoodsPriceDetailEntity> selectByOwnerOrderNo(@Param("ownerOrderNo") String ownerOrderNo);

    int insertList(@Param("list")List<OwnerGoodsPriceDetailEntity> list);

    List<OwnerGoodsPriceDetailEntity> getByOwnerOrderNo(@Param("ownerOrderNo")String ownerOrderNo);
}
