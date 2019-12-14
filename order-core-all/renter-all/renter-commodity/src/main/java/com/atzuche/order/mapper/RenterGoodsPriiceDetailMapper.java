package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterGoodsPriiceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Mapper
public interface RenterGoodsPriiceDetailMapper{

    RenterGoodsPriiceDetailEntity selectByPrimaryKey(Integer id);

    List<RenterGoodsPriiceDetailEntity> selectALL();

    int insert(RenterGoodsPriiceDetailEntity record);
    
    int insertSelective(RenterGoodsPriiceDetailEntity record);

    int updateByPrimaryKey(RenterGoodsPriiceDetailEntity record);
    
    int updateByPrimaryKeySelective(RenterGoodsPriiceDetailEntity record);

    List<RenterGoodsPriiceDetailEntity> listRenterGoodsPriceByOrderNo(@Param("orderNo") Long orderNo);
}
