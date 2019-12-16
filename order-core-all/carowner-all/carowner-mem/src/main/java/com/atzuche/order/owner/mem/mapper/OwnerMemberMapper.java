package com.atzuche.order.owner.mem.mapper;

import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:09:46
 */
@Mapper
public interface OwnerMemberMapper{

    OwnerMemberEntity selectByPrimaryKey(Integer id);

    List<OwnerMemberEntity> selectALL();

    int insert(OwnerMemberEntity record);
    
    int insertSelective(OwnerMemberEntity record);

    int updateByPrimaryKey(OwnerMemberEntity record);
    
    int updateByPrimaryKeySelective(OwnerMemberEntity record);

}
