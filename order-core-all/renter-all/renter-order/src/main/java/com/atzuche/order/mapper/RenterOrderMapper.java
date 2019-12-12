package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:18:40
 */
@Mapper
public interface RenterOrderMapper{

    RenterOrderEntity selectByPrimaryKey(Integer id);

    List<RenterOrderEntity> selectALL();

    int insert(RenterOrderEntity record);
    
    int insertSelective(RenterOrderEntity record);

    int updateByPrimaryKey(RenterOrderEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderEntity record);

}
