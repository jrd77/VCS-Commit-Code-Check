package com.atzuche.order.owner.cost.mapper;

import com.atzuche.order.owner.cost.entity.OwnerOrderSubsidyDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 */
@Mapper
public interface OwnerOrderSubsidyDetailMapper{

    OwnerOrderSubsidyDetailEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderSubsidyDetailEntity> selectALL();

    int insert(OwnerOrderSubsidyDetailEntity record);
    
    int saveOwnerOrderSubsidyDetail(OwnerOrderSubsidyDetailEntity record);

    int updateByPrimaryKey(OwnerOrderSubsidyDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderSubsidyDetailEntity record);
    
    List<OwnerOrderSubsidyDetailEntity> listOwnerOrderSubsidyDetail(@Param("orderNo") String orderNo, @Param("ownerOrderNo") String ownerOrderNo);

    Integer saveOwnerOrderSubsidyDetailBatch(@Param("costList") List<OwnerOrderSubsidyDetailEntity> costList);
}
