package com.atzuche.order.owner.mem.mapper;

import com.atzuche.order.owner.mem.entity.OwnerMemberRightEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主会员权益表
 * 
 * @author ZhangBin
 * @date 2019-12-18 16:15:16
 */
@Mapper
public interface OwnerMemberRightMapper{

    OwnerMemberRightEntity selectByPrimaryKey(Integer id);

    int insert(OwnerMemberRightEntity record);
    
    int insertSelective(OwnerMemberRightEntity record);

    int updateByPrimaryKey(OwnerMemberRightEntity record);
    
    int updateByPrimaryKeySelective(OwnerMemberRightEntity record);

    int insertList(@Param("list")List<OwnerMemberRightEntity> list);

    List<OwnerMemberRightEntity> selectByOwnerOrderNo(@Param("ownerOrderNo")String ownerOrderNo);


}
