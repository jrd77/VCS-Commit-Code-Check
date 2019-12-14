package com.atzuche.order.rentermem.mapper;

import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端会员权益表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Mapper
public interface RenterMemberRightMapper{

    RenterMemberRightEntity selectByPrimaryKey(Integer id);

    List<RenterMemberRightEntity> selectALL();

    int insert(RenterMemberRightEntity record);
    
    int insertSelective(RenterMemberRightEntity record);

    int updateByPrimaryKey(RenterMemberRightEntity record);
    
    int updateByPrimaryKeySelective(RenterMemberRightEntity record);

}
