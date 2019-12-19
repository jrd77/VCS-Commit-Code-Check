package com.atzuche.order.rentermem.mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-18 16:15:16
 */
@Mapper
public interface RenterMemberMapper{

    RenterMemberEntity selectByPrimaryKey(Integer id);

    int insert(RenterMemberEntity record);
    
    int insertSelective(RenterMemberEntity record);

    int updateByPrimaryKey(RenterMemberEntity record);
    
    int updateByPrimaryKeySelective(RenterMemberEntity record);

    RenterMemberEntity selectByRenterOrderNo(@Param("renterOrderNo")String renterOrderNo);

}
