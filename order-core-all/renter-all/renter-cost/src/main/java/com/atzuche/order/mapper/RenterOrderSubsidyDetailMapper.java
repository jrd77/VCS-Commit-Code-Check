package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterOrderSubsidyDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:10:05
 */
@Mapper
public interface RenterOrderSubsidyDetailMapper{

    RenterOrderSubsidyDetailEntity selectByPrimaryKey(Integer id);

    List<RenterOrderSubsidyDetailEntity> selectALL();

    int insert(RenterOrderSubsidyDetailEntity record);
    
    int insertSelective(RenterOrderSubsidyDetailEntity record);

    int updateByPrimaryKey(RenterOrderSubsidyDetailEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderSubsidyDetailEntity record);
    
    RenterOrderSubsidyDetailEntity getRenterOrderSubsidyDetail(@Param("renterOrderNo") String renterOrderNo);

}
