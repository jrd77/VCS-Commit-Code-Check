package com.atzuche.order.renterclaim.mapper;

import com.atzuche.order.renterclaim.entity.RenterEventClaimStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端理赔处理状态表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:50:12
 */
@Mapper
public interface RenterEventClaimStatusMapper{

    RenterEventClaimStatusEntity selectByPrimaryKey(Integer id);

    List<RenterEventClaimStatusEntity> selectALL();

    int insert(RenterEventClaimStatusEntity record);
    
    int insertSelective(RenterEventClaimStatusEntity record);

    int updateByPrimaryKey(RenterEventClaimStatusEntity record);
    
    int updateByPrimaryKeySelective(RenterEventClaimStatusEntity record);

}
