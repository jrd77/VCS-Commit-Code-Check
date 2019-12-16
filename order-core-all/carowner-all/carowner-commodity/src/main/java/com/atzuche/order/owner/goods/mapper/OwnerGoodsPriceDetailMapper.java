package com.atzuche.order.owner.goods.mapper;

import com.atzuche.order.owner.goods.entity.OwnerGoodsPriceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端商品概览价格明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:12:50
 */
@Mapper
public interface OwnerGoodsPriceDetailMapper{

    OwnerGoodsPriceDetailEntity selectByPrimaryKey(Integer id);

    List<OwnerGoodsPriceDetailEntity> selectALL();

    int insert(OwnerGoodsPriceDetailEntity record);
    
    int insertSelective(OwnerGoodsPriceDetailEntity record);

    int updateByPrimaryKey(OwnerGoodsPriceDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerGoodsPriceDetailEntity record);

}
