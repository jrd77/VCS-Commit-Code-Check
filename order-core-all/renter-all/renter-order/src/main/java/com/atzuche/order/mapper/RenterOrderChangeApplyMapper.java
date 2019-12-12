package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterOrderChangeApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单变更申请表
 * 
 * @author ZhangBin
 * @date 2019-12-12 14:29:22
 */
@Mapper
public interface RenterOrderChangeApplyMapper{

    RenterOrderChangeApplyEntity selectByPrimaryKey(Integer id);

    List<RenterOrderChangeApplyEntity> selectALL();

    int insert(RenterOrderChangeApplyEntity record);
    
    int insertSelective(RenterOrderChangeApplyEntity record);

    int updateByPrimaryKey(RenterOrderChangeApplyEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderChangeApplyEntity record);

}
