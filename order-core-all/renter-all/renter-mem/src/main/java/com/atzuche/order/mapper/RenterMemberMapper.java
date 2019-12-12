package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:17:27
 */
@Mapper
public interface RenterMemberMapper{

    RenterMemberEntity selectByPrimaryKey(Integer id);

    List<RenterMemberEntity> selectALL();

    int insert(RenterMemberEntity record);
    
    int insertSelective(RenterMemberEntity record);

    int updateByPrimaryKey(RenterMemberEntity record);
    
    int updateByPrimaryKeySelective(RenterMemberEntity record);

}
