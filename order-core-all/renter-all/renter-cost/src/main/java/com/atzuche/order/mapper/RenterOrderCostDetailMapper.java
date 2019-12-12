package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterOrderCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客费用明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:10:05
 */
@Mapper
public interface RenterOrderCostDetailMapper{

    RenterOrderCostDetailEntity selectByPrimaryKey(Integer id);

    List<RenterOrderCostDetailEntity> selectALL();

    int insert(RenterOrderCostDetailEntity record);
    
    int insertSelective(RenterOrderCostDetailEntity record);

    int updateByPrimaryKey(RenterOrderCostDetailEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderCostDetailEntity record);

}
