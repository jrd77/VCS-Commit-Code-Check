package com.atzuche.order.detain.mapper;

import com.atzuche.order.detain.entity.RenterDetainUnfreezeEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 暂扣解冻表
 * 
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 */
@Mapper
public interface RenterDetainUnfreezeMapper{

    RenterDetainUnfreezeEntity selectByPrimaryKey(Integer id);

    int insertSelective(RenterDetainUnfreezeEntity record);

    int updateByPrimaryKeySelective(RenterDetainUnfreezeEntity record);

}
