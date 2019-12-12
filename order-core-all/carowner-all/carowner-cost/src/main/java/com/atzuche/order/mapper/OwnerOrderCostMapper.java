package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerOrderCostEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 */
@Mapper
public interface OwnerOrderCostMapper{

    OwnerOrderCostEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderCostEntity> selectALL();

    int insert(OwnerOrderCostEntity record);
    
    int insertSelective(OwnerOrderCostEntity record);

    int updateByPrimaryKey(OwnerOrderCostEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderCostEntity record);

}
