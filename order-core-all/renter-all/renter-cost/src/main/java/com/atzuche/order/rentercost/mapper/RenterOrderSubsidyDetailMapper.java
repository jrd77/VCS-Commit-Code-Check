package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客补贴明细表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:30:57
 */
@Mapper
public interface RenterOrderSubsidyDetailMapper{

    RenterOrderSubsidyDetailEntity selectByPrimaryKey(Integer id);

    List<RenterOrderSubsidyDetailEntity> selectALL();

    int insert(RenterOrderSubsidyDetailEntity record);
    
    int saveRenterOrderSubsidyDetail(RenterOrderSubsidyDetailEntity record);

    int updateByPrimaryKey(RenterOrderSubsidyDetailEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderSubsidyDetailEntity record);
    
    List<RenterOrderSubsidyDetailEntity> listRenterOrderSubsidyDetail(@Param("orderNo") String orderNo, @Param("renterOrderNo") String renterOrderNo);

    Integer saveRenterOrderSubsidyDetailBatch(@Param("costList") List<RenterOrderSubsidyDetailEntity> costList);
}
