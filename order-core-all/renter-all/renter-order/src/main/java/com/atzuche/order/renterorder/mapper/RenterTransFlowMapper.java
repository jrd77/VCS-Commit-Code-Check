package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterTransFlowEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端交易流程表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:46:44
 */
@Mapper
public interface RenterTransFlowMapper{

    RenterTransFlowEntity selectByPrimaryKey(Integer id);

    int insert(RenterTransFlowEntity record);
    
    int insertSelective(RenterTransFlowEntity record);

    int updateByPrimaryKey(RenterTransFlowEntity record);
    
    int updateByPrimaryKeySelective(RenterTransFlowEntity record);

}
