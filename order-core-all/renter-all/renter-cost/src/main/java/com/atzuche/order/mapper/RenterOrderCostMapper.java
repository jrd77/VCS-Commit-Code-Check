package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterOrderCostEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:10:06
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
