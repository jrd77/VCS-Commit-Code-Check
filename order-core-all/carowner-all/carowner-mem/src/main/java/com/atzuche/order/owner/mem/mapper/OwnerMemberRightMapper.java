package com.atzuche.order.owner.mem.mapper;

import com.atzuche.order.owner.mem.entity.OwnerMemberRightEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主会员权益表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:09:46
 */
@Mapper
public interface OwnerMemberRightMapper{

    OwnerMemberRightEntity selectByPrimaryKey(Integer id);

    List<OwnerMemberRightEntity> selectALL();

    int insert(OwnerMemberRightEntity record);
    
    int insertSelective(OwnerMemberRightEntity record);

    int updateByPrimaryKey(OwnerMemberRightEntity record);
    
    int updateByPrimaryKeySelective(OwnerMemberRightEntity record);

}
