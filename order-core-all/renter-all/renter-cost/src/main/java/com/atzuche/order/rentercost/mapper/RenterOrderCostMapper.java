package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Mapper
public interface RenterOrderCostMapper{

    RenterOrderCostEntity selectByPrimaryKey(Integer id);

    List<RenterOrderCostEntity> selectALL();

    int insert(RenterOrderCostEntity record);
    
    int insertSelective(RenterOrderCostEntity record);

    int updateByPrimaryKey(RenterOrderCostEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderCostEntity record);

}
