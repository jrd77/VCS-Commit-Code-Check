package com.atzuche.order.rentercommodity.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-17 11:43:15
 */
@Mapper
public interface RenterGoodsPriceDetailMapper{

    RenterGoodsPriceDetailEntity selectByPrimaryKey(Integer id);

    int insert(RenterGoodsPriceDetailEntity record);
    
    int insertSelective(RenterGoodsPriceDetailEntity record);

    int updateByPrimaryKey(RenterGoodsPriceDetailEntity record);
    
    int updateByPrimaryKeySelective(RenterGoodsPriceDetailEntity record);

    List<RenterGoodsPriceDetailEntity> selectByRenterOrderNo(@Param("renterOrderNo")String renterOrderNo);

    int insertList(@Param("list")List<RenterGoodsPriceDetailEntity> list);


}
