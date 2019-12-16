package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerOrderChangeApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客变更订单申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 */
@Mapper
public interface OwnerOrderChangeApplyMapper{

    OwnerOrderChangeApplyEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderChangeApplyEntity> selectALL();

    int insert(OwnerOrderChangeApplyEntity record);
    
    int insertSelective(OwnerOrderChangeApplyEntity record);

    int updateByPrimaryKey(OwnerOrderChangeApplyEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderChangeApplyEntity record);

}
