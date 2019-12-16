package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客费用明细表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:30:57
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
