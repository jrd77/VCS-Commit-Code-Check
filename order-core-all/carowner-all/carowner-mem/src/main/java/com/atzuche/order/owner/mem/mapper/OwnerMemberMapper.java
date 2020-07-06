package com.atzuche.order.owner.mem.mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-18 16:15:16
 */
@Mapper
public interface OwnerMemberMapper{

    OwnerMemberEntity selectByPrimaryKey(Integer id);

    int insert(OwnerMemberEntity record);
    
    int insertSelective(OwnerMemberEntity record);

    int updateByPrimaryKey(OwnerMemberEntity record);
    
    int updateByPrimaryKeySelective(OwnerMemberEntity record);

    OwnerMemberEntity selectByOwnerOrderNo(@Param("ownerOrderNo")String ownerOrderNo);

    String getOwnerNoByOrderNo(@Param("orderNo") String orderNo);

    List<OwnerMemberEntity> queryMemNoAndPhoneByOrderList(@Param("orderNos") List<String> orderNos);

    OwnerMemberEntity queryOwnerInfoByOrderNoAndOwnerNo(@Param("orderNo") String orderNo,@Param("ownerNo") String ownerNo);

    OwnerMemberEntity queryOwnerMemberEntityByOrderNoAndOwnerNo(@Param("orderNo") String orderNo,@Param("ownerNo") String ownerNo);

    OwnerMemberEntity getOwnerNoByMemberNo(@Param("memNo") String memNo);
}
