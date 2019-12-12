package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterGoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客商品概览表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Mapper
public interface RenterGoodsMapper{

    RenterGoodsEntity selectByPrimaryKey(Integer id);

    List<RenterGoodsEntity> selectALL();

    int insert(RenterGoodsEntity record);
    
    int insertSelective(RenterGoodsEntity record);

    int updateByPrimaryKey(RenterGoodsEntity record);
    
    int updateByPrimaryKeySelective(RenterGoodsEntity record);

}
