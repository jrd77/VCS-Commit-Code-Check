package com.atzuche.order.owner.goods.mapper;

import com.atzuche.order.owner.goods.entity.OwnerGoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端商品概览表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:12:50
 */
@Mapper
public interface OwnerGoodsMapper{

    OwnerGoodsEntity selectByPrimaryKey(Integer id);

    List<OwnerGoodsEntity> selectALL();

    int insert(OwnerGoodsEntity record);
    
    int insertSelective(OwnerGoodsEntity record);

    int updateByPrimaryKey(OwnerGoodsEntity record);
    
    int updateByPrimaryKeySelective(OwnerGoodsEntity record);

}
