package com.atzuche.order.detain.mapper;

import com.atzuche.order.detain.entity.RenterEventDetainEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端暂扣处理事件表
 * 
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 */
@Mapper
public interface RenterEventDetainMapper{

    RenterEventDetainEntity selectByPrimaryKey(Integer id);

    int insertSelective(RenterEventDetainEntity record);

    int updateByPrimaryKeySelective(RenterEventDetainEntity record);

}
