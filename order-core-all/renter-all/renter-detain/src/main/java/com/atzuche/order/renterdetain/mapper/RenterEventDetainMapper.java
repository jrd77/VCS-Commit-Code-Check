package com.atzuche.order.renterdetain.mapper;

import com.atzuche.order.renterdetain.entity.RenterEventDetainEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端暂扣处理事件表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 */
@Mapper
public interface RenterEventDetainMapper{

    RenterEventDetainEntity selectByPrimaryKey(Integer id);

    List<RenterEventDetainEntity> selectALL();

    int insert(RenterEventDetainEntity record);
    
    int insertSelective(RenterEventDetainEntity record);

    int updateByPrimaryKey(RenterEventDetainEntity record);
    
    int updateByPrimaryKeySelective(RenterEventDetainEntity record);

}
