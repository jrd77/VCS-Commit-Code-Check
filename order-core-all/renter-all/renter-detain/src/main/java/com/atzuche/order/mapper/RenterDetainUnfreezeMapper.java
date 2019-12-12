package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterDetainUnfreezeEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 暂扣解冻表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 */
@Mapper
public interface RenterDetainUnfreezeMapper{

    RenterDetainUnfreezeEntity selectByPrimaryKey(Integer id);

    List<RenterDetainUnfreezeEntity> selectALL();

    int insert(RenterDetainUnfreezeEntity record);
    
    int insertSelective(RenterDetainUnfreezeEntity record);

    int updateByPrimaryKey(RenterDetainUnfreezeEntity record);
    
    int updateByPrimaryKeySelective(RenterDetainUnfreezeEntity record);

}
