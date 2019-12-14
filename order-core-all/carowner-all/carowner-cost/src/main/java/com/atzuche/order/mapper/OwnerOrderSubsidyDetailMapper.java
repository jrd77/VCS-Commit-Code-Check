package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerOrderSubsidyDetailEntity;
import org.apache.ibatis.annotations.Mapper;
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
    
    int insertSelective(OwnerOrderSubsidyDetailEntity record);

    int updateByPrimaryKey(OwnerOrderSubsidyDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderSubsidyDetailEntity record);

}
