package com.atzuche.order.renterclaim.mapper;

import com.atzuche.order.renterclaim.entity.RenterEventClaimEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端理赔处理事件表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:50:12
 */
@Mapper
public interface RenterEventClaimMapper{

    RenterEventClaimEntity selectByPrimaryKey(Integer id);

    List<RenterEventClaimEntity> selectALL();

    int insert(RenterEventClaimEntity record);
    
    int insertSelective(RenterEventClaimEntity record);

    int updateByPrimaryKey(RenterEventClaimEntity record);
    
    int updateByPrimaryKeySelective(RenterEventClaimEntity record);

}
