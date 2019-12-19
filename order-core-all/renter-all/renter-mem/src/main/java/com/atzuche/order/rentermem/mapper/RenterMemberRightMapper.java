package com.atzuche.order.rentermem.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租客端会员权益表
 * 
 * @author ZhangBin
 * @date 2019-12-18 16:15:16
 */
@Mapper
public interface RenterMemberRightMapper{

    RenterMemberRightEntity selectByPrimaryKey(Integer id);

    int insert(RenterMemberRightEntity record);
    
    int insertSelective(RenterMemberRightEntity record);

    int updateByPrimaryKey(RenterMemberRightEntity record);
    
    int updateByPrimaryKeySelective(RenterMemberRightEntity record);

    int insertList(@Param("list")List<RenterMemberRightEntity> list);

    List<RenterMemberRightEntity> selectByRenterOrderNo(@Param("renterOrderNo")String renterOrderNo);

}
