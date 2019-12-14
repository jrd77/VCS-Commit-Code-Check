package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerOrderIncrementDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主增值订单明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 */
@Mapper
public interface OwnerOrderIncrementDetailMapper{

    OwnerOrderIncrementDetailEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderIncrementDetailEntity> selectALL();

    int insert(OwnerOrderIncrementDetailEntity record);
    
    int insertSelective(OwnerOrderIncrementDetailEntity record);

    int updateByPrimaryKey(OwnerOrderIncrementDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderIncrementDetailEntity record);

}
