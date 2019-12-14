package com.atzuche.order.renterdetain.mapper;

import com.atzuche.order.renterdetain.entity.RenterEventDetainStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端暂扣处理状态表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 */
@Mapper
public interface RenterEventDetainStatusMapper{

    RenterEventDetainStatusEntity selectByPrimaryKey(Integer id);

    List<RenterEventDetainStatusEntity> selectALL();

    int insert(RenterEventDetainStatusEntity record);
    
    int insertSelective(RenterEventDetainStatusEntity record);

    int updateByPrimaryKey(RenterEventDetainStatusEntity record);
    
    int updateByPrimaryKeySelective(RenterEventDetainStatusEntity record);

}
