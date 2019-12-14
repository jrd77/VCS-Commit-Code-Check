package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单变更申请表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
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
