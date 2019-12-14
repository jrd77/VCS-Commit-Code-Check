package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerOrderOperateLogEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主订单操作记录表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 */
@Mapper
public interface OwnerOrderOperateLogMapper{

    OwnerOrderOperateLogEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderOperateLogEntity> selectALL();

    int insert(OwnerOrderOperateLogEntity record);
    
    int insertSelective(OwnerOrderOperateLogEntity record);

    int updateByPrimaryKey(OwnerOrderOperateLogEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderOperateLogEntity record);

}
